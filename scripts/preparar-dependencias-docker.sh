#!/usr/bin/env bash
set -euo pipefail

SCRIPT_DIR="$(cd "$(dirname "${BASH_SOURCE[0]}")" && pwd)"
PROJECT_ROOT="$(cd "$SCRIPT_DIR/.." && pwd)"
MAVEN_REPOSITORY_DIR="${1:-$PROJECT_ROOT/docker/maven-repository}"
MAVEN_REPOSITORY_SOURCE="${MAVEN_REPOSITORY_SOURCE:-$HOME/.m2/repository}"

modules=(
  eureka
  config-server
  api-gateway
  videojuegos
  usuarios
  authentication
  carrito
  pagos
  pedidos
  resenas
  inventario
)

mkdir -p "$MAVEN_REPOSITORY_DIR"
touch "$MAVEN_REPOSITORY_DIR/.gitkeep"

echo "Preparando repositorio Maven local para Docker"
echo "Destino: $MAVEN_REPOSITORY_DIR"
echo

if [ -d "$MAVEN_REPOSITORY_SOURCE" ]; then
  echo "Copiando cache Maven local desde:"
  echo "$MAVEN_REPOSITORY_SOURCE"
  if command -v rsync >/dev/null 2>&1; then
    rsync -a --exclude "*.lastUpdated" \
      "$MAVEN_REPOSITORY_SOURCE/" "$MAVEN_REPOSITORY_DIR/"
  else
    cp -a "$MAVEN_REPOSITORY_SOURCE/." "$MAVEN_REPOSITORY_DIR/"
  fi
  echo
fi

for module in "${modules[@]}"; do
  module_dir="$PROJECT_ROOT/$module"

  if [ ! -f "$module_dir/pom.xml" ]; then
    echo "WARN $module: no existe pom.xml, se omite"
    continue
  fi

  echo "==> Descargando dependencias de $module"
  (
    cd "$module_dir"
    ./mvnw -o -Dmaven.repo.local="$MAVEN_REPOSITORY_DIR" -DskipTests package \
      || ./mvnw -Dmaven.repo.local="$MAVEN_REPOSITORY_DIR" -DskipTests package
  )
done

find "$MAVEN_REPOSITORY_DIR" -name "*.lastUpdated" -delete

echo
echo "Dependencias listas en:"
echo "$MAVEN_REPOSITORY_DIR"
echo
du -sh "$MAVEN_REPOSITORY_DIR" 2>/dev/null || true
