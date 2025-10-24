
(
  cd k8s
  source stop.sh
)

echo "Building everything. Load generator."

./gradlew build


echo "Building load generator image"
(
  cd load-generator
  source build-image.sh
)

echo "Building server image"
(
  cd server
  source build-image.sh
)

echo "Starting k8s"
(
  cd k8s/
  source lint.sh

  echo "Start and wait to be ready"
  minikube start
  minikube addons enable metrics-server
  minikube kubectl -- wait --for=condition=Ready     -n kube-system --timeout=60s pod --all
  minikube kubectl -- wait --for=condition=Available -n kube-system --timeout=60s deployment/metrics-server

  # minikube kubectl -- get deployment metrics-server -n kube-system -o json | jq '.spec.template.spec.containers[0].args'
  echo "Patch metrics server (for HPA)"
  minikube kubectl -- patch deployment metrics-server -n kube-system --patch-file k8s-patch-for-metrics-server.yaml
  minikube kubectl -- rollout status deployment/metrics-server -n kube-system
  minikube kubectl -- wait --for=condition=Ready     -n kube-system --timeout=60s pod --all
  minikube kubectl -- wait --for=condition=Available -n kube-system --timeout=60s deployment/metrics-server

  # Install KEDA (K8S event driven auto-scaling)
#  minikube kubectl -- apply --server-side -f https://github.com/kedacore/keda/releases/download/v2.11.2/keda-2.11.2.yaml
#  minikube kubectl -- wait pod --all --for=condition=Ready --namespace=keda --timeout=120s

  # Upload into minikube, the load generator image
  minikube image load case-study-server:2025-10-09
  minikube image load load-generator:2025-10-08

  minikube kubectl -- apply -f k8s-name-space.yaml
  minikube kubectl -- apply -f k8s-case-study-server.yaml
  minikube start --extra-config=controller-manager.horizontal-pod-autoscaler-sync-period=10s
  minikube kubectl -- wait pod --all --for=condition=Ready --namespace=case-study --timeout=120s

  minikube kubectl -- apply -f k8s-load-generation.yaml
  minikube kubectl -- wait pod --all --for=condition=Ready --namespace=case-study --timeout=120s

  source current-state.sh

  source curl.sh

  #  USEFUL
  #  kubectl logs -f -n case-study load-generator
  #   kubectl exec -it -n case-study pod/load-generator -- sh
  #  Debug KEDA
  # kubectl logs -n keda deployment/keda-operator --tail=100 -f

  set -x
)
