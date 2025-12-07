#!/bin/bash

set -e

./generate-key-pair-cert.sh alice
./generate-key-pair-cert.sh bob

set +e
