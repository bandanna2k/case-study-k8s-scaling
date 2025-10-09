
source stop.sh

echo "Building everything. Load generator."

./gradlew build


echo "Building load generator image"
(
  cd load-generator

  source build-image.sh
)

echo "Starting k8s"
(
  set -e

  cd k8s/

  source lint.sh

  # Start and wait to be ready
  minikube start
  minikube addons enable metrics-server
  minikube kubectl -- wait pod --all --for=condition=Ready --namespace=kube-system --timeout=60s

  # Upload into minikube, the load generator image
  minikube image load load-generator:2025-10-08

  minikube kubectl -- apply -f k8s-name-space.yaml
  minikube kubectl -- apply -f k8s-processing.yaml
  minikube kubectl -- wait pod --all --for=condition=Ready --namespace=case-study --timeout=120s

  minikube kubectl -- apply -f k8s-load-generation.yaml
  minikube kubectl -- wait pod --all --for=condition=Ready --namespace=case-study --timeout=120s

  source current-state.sh

  source curl.sh

  #  USEFUL
  #  kubectl logs -f -n case-study load-generator
  #  kubectl exec -it  pod/load-generator -- sh

  set -x
)
