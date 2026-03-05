#!/bin/sh
# Maven Wrapper script
# https://maven.apache.org/wrapper/

set -euf
[ "${MVNW_VERBOSE-}" != true ] || set -x

# OS specific support.
case "$(uname -s)" in
CYGWIN* | MINGW* | MSYS*)
  MAVEN_HOME="$(cd "$(dirname "$0")" && pwd -W)"
  ;;
*)
  MAVEN_HOME="$(cd "$(dirname "$0")" && pwd)"
  ;;
esac

MAVEN_PROJECTBASEDIR="${MAVEN_BASEDIR:-$MAVEN_HOME}"
MAVEN_WRAPPER_JAR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.jar"
MAVEN_WRAPPER_PROPERTIES="$MAVEN_PROJECTBASEDIR/.mvn/wrapper/maven-wrapper.properties"

if [ -r "$MAVEN_WRAPPER_PROPERTIES" ]; then
  MAVEN_URL=$(grep -E '^distributionUrl=' "$MAVEN_WRAPPER_PROPERTIES" | sed 's/distributionUrl=//')
fi

MAVEN_URL="${MAVEN_URL:-https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/3.9.6/apache-maven-3.9.6-bin.zip}"

MAVEN_BASEDIR="$MAVEN_PROJECTBASEDIR/.mvn/wrapper"
MAVEN_ZIP="$MAVEN_BASEDIR/apache-maven.zip"
MAVEN_DIR="$MAVEN_BASEDIR/apache-maven"

if [ ! -d "$MAVEN_DIR" ]; then
  mkdir -p "$MAVEN_BASEDIR"
  echo "Downloading Maven from $MAVEN_URL..."
  if command -v curl > /dev/null 2>&1; then
    curl -fsSL -o "$MAVEN_ZIP" "$MAVEN_URL"
  elif command -v wget > /dev/null 2>&1; then
    wget -q -O "$MAVEN_ZIP" "$MAVEN_URL"
  else
    echo "Error: curl or wget is required to download Maven."
    exit 1
  fi
  unzip -q -d "$MAVEN_BASEDIR" "$MAVEN_ZIP"
  mv "$MAVEN_BASEDIR"/apache-maven-*/ "$MAVEN_DIR"
  rm -f "$MAVEN_ZIP"
fi

exec "$MAVEN_DIR/bin/mvn" "$@"
