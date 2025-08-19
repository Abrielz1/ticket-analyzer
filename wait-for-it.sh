#!/bin/sh
# wait-for-it.sh

set -e

host="$1"
shift
cmd="$@"

until nc -z -v -w30 "$host" 2>/dev/null; do
  >&2 echo "[WAIT] Waiting for $host to be available..."
  sleep 1
done

>&2 echo "[INFO] $host is up - executing command: $cmd"
exec $cmd