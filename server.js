const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const { Pool } = require('pg');
const admin = require('./firebase-admin');

const app = express();
const port = process.env.PORT || 5000;

// Configuración de PostgreSQL
const pool = new Pool({
  connectionString: process.env.DATABASE_URL,
  ssl: process.env.NODE_ENV === 'production' ? { rejectUnauthorized: false } : false
});

// Inicialización de la base de datos
async function initDB() {
  try {
    await pool.query(`
      CREATE TABLE IF NOT EXISTS users (
        firebase_uid TEXT PRIMARY KEY,
        email TEXT UNIQUE NOT NULL,
        is_premium BOOLEAN DEFAULT FALSE,
        urls_created_this_month INT DEFAULT 0,
        last_reset_date TIMESTAMP DEFAULT NOW()
      );
    `);

    await pool.query(`
      CREATE TABLE IF NOT EXISTS shortened_urls (
        id SERIAL PRIMARY KEY,
        short_code TEXT UNIQUE NOT NULL,
        original_url TEXT NOT NULL,
        user_uid TEXT REFERENCES users(firebase_uid),
        created_at TIMESTAMP DEFAULT NOW()
      );
    `);
    console.log("Tablas de DB inicializadas correctamente");
  } catch (err) {
    console.error("Error al inicializar DB:", err);
    throw err; // Relanzar el error para manejo adicional si es necesario
  }
}

// Middlewares
app.use(cors());
app.use(bodyParser.json());

async function startServer() {
    try {
      await initDB();
      app.listen(port, () => {
        console.log(`Servidor corriendo en http://localhost:${port}`);
      });
    } catch (err) {
      console.error("No se pudo iniciar el servidor:", err);
      process.exit(1); 
    }
  }

startServer();

// Inicializar la base de datos al iniciar
initDB().then(() => {
  console.log("Base de datos lista");
}).catch(err => {
  console.error("Error crítico al inicializar DB:", err);
});

// Ruta para acortar la URL
app.post('/shorten', async (req, res) => {
  const { originalUrl, shortCode, token } = req.body;

  if (!originalUrl || !shortCode || !token) {
    return res.status(400).json({ error: 'Faltan parámetros' });
  }

  try {
    // Verificar usuario
    const { uid } = await admin.auth().verifyIdToken(token);

    // Guardar en PostgreSQL
    await pool.query(
      `INSERT INTO shortened_urls (short_code, original_url, user_uid)
       VALUES ($1, $2, $3)`,
      [shortCode, originalUrl, uid]
    );

    res.json({ 
      message: 'URL acortada guardada exitosamente',
      shortUrl: `https://urlshortener-production-3cf7.up.railway.app${shortCode}`
    });
  } catch (error) {
    console.error("Error al acortar URL:", error);
    if (error.code === '23505') {
      res.status(409).json({ error: 'El código corto ya existe' });
    } else {
      res.status(500).json({ error: 'Error del servidor', details: error.message });
    }
  }
});

// Ruta para redirigir a la URL original
app.get('/:code', async (req, res) => {
  const code = req.params.code;

  try {
    const result = await pool.query(
      `SELECT original_url FROM shortened_urls 
       WHERE short_code = $1`,
      [code]
    );

    if (result.rows.length > 0) {
      console.log(`Redirigiendo ${code} a ${result.rows[0].original_url}`);
      res.redirect(result.rows[0].original_url);
    } else {
      res.status(404).send('URL no encontrada');
    }
  } catch (error) {
    console.error("Error al redirigir:", error);
    res.status(500).send('Error interno del servidor');
  }
});

// Ruta de salud
app.get('/', (req, res) => {
  res.send('Servidor de URL Shortener funcionando');
});

// Manejo de errores global
app.use((err, req, res, next) => {
  console.error("Error no manejado:", err);
  res.status(500).json({ error: 'Error interno del servidor' });
});

// Iniciar el servidor
app.listen(port, () => {
  console.log(`Servidor corriendo en http://localhost:${port}`);
});