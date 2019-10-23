// FIXME: turn to proxy.conf.json!
const PROXY_CONFIG = {
  '/api': {
    target: 'http://localhost:9000',
    secure: false
  },
};

module.exports = PROXY_CONFIG;
