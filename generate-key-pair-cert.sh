#!/bin/bash

set -e

ALGORITHM="rsa:2048"
VALIDITY_IN_DAYS=365

NAME=$1

if [ -z "$NAME" ]; then
  echo "Usage: $0 <name>"
  exit 1
fi

mkdir -p "src/main/resources/keys/${NAME}"

KEY_FILE="src/main/resources/keys/${NAME}/key.pem"
CERT_FILE="src/main/resources/keys/${NAME}/cert.pem"

# Generate private key + self-signed certificate in one command
MSYS_NO_PATHCONV=1 openssl req \
  -x509 \
  -newkey ${ALGORITHM} \
  -keyout "${KEY_FILE}" \
  -out "${CERT_FILE}" \
  -sha256 \
  -days "${VALIDITY_IN_DAYS}" \
  -nodes -subj "/C=MK/ST=Skopje/L=Skopje/O=FINKI/OU=CyberMACS/CN=${NAME}"

echo "Generated key ${KEY_FILE} and certificate ${CERT_FILE}"

set +e
