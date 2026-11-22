// En producción el front llama a /api y Netlify reenvía esas peticiones
// al backend de la VM (ver netlify.toml).
export const environment = {
  apiUrl: '/api'
};
