// server.js
const express = require('express');
const bodyParser = require('body-parser');
const cors = require('cors');
const app = express();
const port = 5000;

app.use(cors());
app.use(bodyParser.json());

// Almacenamiento en memoria (clave: código corto, valor: URL original)
const urlMap = {};

// Ruta para acortar la URL
app.post('/shorten', (req, res) => {
    const { originalUrl, shortCode } = req.body;

    if (!originalUrl || !shortCode) {
        return res.status(400).json({ error: 'Faltan parámetros' });
    }

    urlMap[shortCode] = originalUrl;
    console.log(`Guardado: ${shortCode} → ${originalUrl}`);
    res.json({ message: 'URL acortada guardada exitosamente' });
});

// Ruta para redirigir a la URL original
app.get('/:code', (req, res) => {
    const code = req.params.code;
    const originalUrl = urlMap[code];

    if (originalUrl) {
        console.log(`Redirigiendo ${code} a ${originalUrl}`);
        res.redirect(originalUrl);
    } else {
        res.status(404).send('URL no encontrada');
    }
});

// Iniciar el servidor
app.listen(port, () => {
    console.log(`Servidor corriendo en http://localhost:${port}`);
});
