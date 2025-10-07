

## Useful Commands

- Get image hash from registry (This is because the linter does not like :latest)

```
curl -L -s https://registry.k8s.io/v2/hpa-example/tags/list

{"child":[],"manifest":{"sha256:581697a37f0e136db86d6b30392f0db40ce99c8248a7044c770012f4e8491544":{"mediaType":"application/vnd.docker.distribution.manifest.v2+json","tag":["latest"],"timeUploadedMs":"1660697581976","timeCreatedMs":"1445246221620","imageSizeBytes":"164026588"}},"name":"k8s-artifacts-prod/images/hpa-example","tags":["latest"]}
```

## TODO

- Bazel with Vue/Vite
- Bazel with Maven
- Load generating container

- Gradle with Vue/Vite
- Load generating container
