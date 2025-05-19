// server.js
const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const app = express();
const port = 5000;

app.use(cors());
app.use(bodyParser.json());

// Almacenamiento en memoria (clave: código corto, valor: URL original)
//const urlMap = {};

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
      console.log("Tablas de DB listas");
    } catch (err) {
      console.error("Error al inicializar DB:", err);
    }
  }
  initDB();
  
  app.use(cors());
  app.use(bodyParser.json());

// Ruta para acortar la URL
app.post('/shorten', async (req, res) => {
    const { originalUrl, shortCode, token } = req.body; // Añade token

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
            shortUrl: `https://urlshortener-production-3cf7.up.railway.app${shortCode}` // Ajusta tu dominio
        });
    } catch (error) {
        console.error("Error al acortar URL:", error);
        if (error.code === '23505') { // Violación de unique key
            res.status(409).json({ error: 'El código corto ya existe' });
        } else {
            res.status(500).json({ error: 'Error del servidor' });
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
        res.status(500).send('Error interno');
    }
});

// Ruta de diagnóstico (recuerda eliminarla después)
app.get('/debug/db', async (req, res) => {
    try {
      // Listar tablas existentes
      const tables = await pool.query(`
        SELECT table_name 
        FROM information_schema.tables 
        WHERE table_schema = 'public'
      `);
  
      // Obtener datos de cada tabla
      const users = await pool.query('SELECT * FROM users');
      const urls = await pool.query('SELECT * FROM shortened_urls');
  
      res.json({
        tables: tables.rows,
        users: users.rows,
        urls: urls.rows
      });
    } catch (error) {
      res.status(500).json({ error: error.message });
    }
  });

// Iniciar el servidor
app.listen(port, () => {
    console.log(`Servidor corriendo en http://localhost:${port}`);
  });
