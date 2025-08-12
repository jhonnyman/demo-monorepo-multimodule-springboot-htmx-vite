#  This script calculates the integrity of an HTTP resource based on the SHA-384 digest algorithm

RESOURCE_URL=$0

CALCULATED_DIGEST=$(curl -s $RESOURCE_URL | openssl dgst -sha384 -binary | openssl base64 -A)

echo "Add this to the HTML tag <script integrity=\"sha384-$CALCULATED_DIGEST\" />"