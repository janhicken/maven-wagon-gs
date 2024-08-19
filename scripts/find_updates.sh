#!/usr/bin/env bash
set -o errexit -o nounset -o pipefail

CURL_OPTS=(--fail --silent --show-error)

find_latest_bzlmod_version() {
  local module metadata_url
  module=$1

  metadata_url=https://raw.githubusercontent.com/bazelbuild/bazel-central-registry/main/modules/$module/metadata.json
  curl "${CURL_OPTS[@]}" --location "$metadata_url" |
    jq --raw-output '.versions[]' |
    grep --extended-regexp --invert-match 'alpha|beta|rc' |
    sort --reverse --version-sort |
    head --lines=1
}

check_version() {
  local artifact_id current_version latest_version
  artifact_id=$1
  current_version=$2
  latest_version=$3

  if [[ "$current_version" != "$latest_version" ]]; then
    printf '%s [%s -> %s]\n' "$artifact_id" "$current_version" "$latest_version"
  fi
}

bazel mod deps '<root>' --output json |
  jq --raw-output '.dependencies[].key' |
  while IFS=@ read -r module version; do
    latest_version=$(find_latest_bzlmod_version "$module" "$version")
    check_version "$module" "$version" "$latest_version"
  done
