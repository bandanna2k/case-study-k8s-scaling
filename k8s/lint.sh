PARENT_DIR=`realpath ..`

docker run                                            \
  --rm                                                \
  -v .:/dir                                           \
  -v ./lint/config.yaml:/etc/config.yaml  \
  stackrox/kube-linter                                \
  lint /dir --config /etc/config.yaml

#
#  docker run
#  -v /path/to/files/you/want/to/lint:/dir
#  -v /path/to/config.yaml:/etc/config.yaml
#  stackrox/kube-linter lint /dir --config /etc/config.yaml
