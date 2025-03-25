module.exports = [
    {
        context : ['/api'],
        target : 'http://localhost:8080',
        secure : false,
        changeOrigin: true,
        logLevel : 'debug'

    }
]

// ng serve --proxy-config=proxy.config.js