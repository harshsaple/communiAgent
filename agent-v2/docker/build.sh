#!/bin/bash

# Strict Mode
set -eu

current_dir=$(cd "$(dirname "$0")" && pwd -P)
root_dir=$(dirname "${current_dir}")

push_images=yes

expected_vars=(
    VERSION
    PRIVATE_IMAGE_REGISTRY
    CI_REGISTRY_IMAGE
    CI_PIPELINE_ID
    CI_COMMIT_SHORT_SHA
    CI_COMMIT_REF_NAME
    CI_DEFAULT_BRANCH
)

if [[ ! -n "${CI_PIPELINE_ID-}" ]]; then
    echo "[INFO] Detected local developer build, images will not be pushed."
    unset push_images

    if [[ ! -s "${root_dir}/.env" ]]; then
        echo >&2 "[WARNING] Environment file ${root_dir}/.env missing or empty."
        [[ -f "${root_dir}/.env" ]] || touch "${root_dir}/.env"
    else
        echo "[INFO] Sourcing ${root_dir}/.env"
        source "${root_dir}/.env"
    fi
fi

for var in ${expected_vars[@]}; do
    if [[ ! -n "${!var-}" ]]; then
        echo >&2 "[ERROR] Missing expected var: ${var}"
    fi
done

docker_image_tag=${VERSION}.${CI_PIPELINE_ID}.${CI_COMMIT_SHORT_SHA}

docker_image_list=(
    word-count-web-server
    word-count-plugin-main
    product-server
)

for docker_image_name in ${docker_image_list[@]}; do
    # Pull for cache purpose
    docker pull ${CI_REGISTRY_IMAGE}/${docker_image_name}:latest 2>/dev/null || true

    (
        cd ${root_dir}/${docker_image_name}
        docker build \
            --build-arg PRIVATE_IMAGE_REGISTRY=$PRIVATE_IMAGE_REGISTRY \
            --cache-from ${CI_REGISTRY_IMAGE}/${docker_image_name}:latest \
            --tag ${CI_REGISTRY_IMAGE}/${docker_image_name}:${docker_image_tag} \
            .
    )

    if [[ "${CI_COMMIT_REF_NAME}" == "${CI_DEFAULT_BRANCH}" ]]; then
        # Only apply `latest` tag when building default branch
        docker tag \
            ${CI_REGISTRY_IMAGE}/${docker_image_name}:${docker_image_tag} \
            ${CI_REGISTRY_IMAGE}/${docker_image_name}:latest
    fi

    if [[ -n "${push_images-}" ]]; then
        docker push --all-tags ${CI_REGISTRY_IMAGE}/${docker_image_name}
    fi
done
