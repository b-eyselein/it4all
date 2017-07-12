# Install openssl
apk update && apk add openssl

# Create the SSL Certificate
echo "Creatin certificate"
openssl req -x509 -nodes -days 365 -newkey rsa:2048 \
    -subj       "/C=DE/ST=Bavaria/L=Wuerzburg/O=Company Name/OU=Org/CN=www.example.com" \
    -keyout     /etc/ssl/private/nginx-selfsigned.key \
    -out        /etc/ssl/certs/nginx-selfsigned.crt

# Start nginx
echo "Starting nginx..."
nginx -g "daemon off;"