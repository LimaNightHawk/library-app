export const oktaConfig = {
    clientId: '0oak3s0805ZSWTN9b5d7',
    issuer: 'https://dev-46393502.okta.com/oauth2/default',
    redirectUri: 'http://localhost:3000/login/callback',
    scopes: ['openid', 'profile', 'email'],
    pkce: true,
    disableHttpsChecks: true
}