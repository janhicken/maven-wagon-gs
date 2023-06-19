#!/usr/bin/env bash
set -o errexit
set -o nounset
set -o pipefail

LOCAL_REPO_URL="file://$HOME/.m2/repository"
SONATYPE_REPO_URL='https://oss.sonatype.org/service/local/staging/deploy/maven2'
readonly LOCAL_REPO_URL SONATYPE_REPO_URL

print_usage() {
  printf 'Usage: %s local|maven_central\n' "$0" >&2
}

if [[ $# -ne 1 ]]; then
  print_usage
  exit 1
fi

declare -a run_args
case "$1" in
  local)
    run_args+=(--define maven_repo="$LOCAL_REPO_URL")
    ;;
  maven_central)
    run_args+=(
      --define gpg_sign=True
      --define maven_repo="$SONATYPE_REPO_URL"
      --define maven_user="$SONATYPE_USER"
      --define maven_password="$SONATYPE_PASSWORD"
    )
    ;;
  *)
    print_usage
    exit 1
    ;;
esac

exec bazel run --stamp "${run_args[@]}" //:maven.publish
