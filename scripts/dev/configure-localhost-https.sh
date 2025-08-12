# See instructions on https://github.com/FiloSottile/mkcert
sudo dnf install certutil nss-tools mkcert

mkcert -key-file development/certs/key.pem -cert-file development/certs/cert.pem localhost

openssl pkcs12 -export \
  -in development/certs/cert.pem \
  -inkey development/certs/key.pem \
  -out demo-app/src/main/resources/keystore.p12 \
  -name demo-app \
  -password pass:password