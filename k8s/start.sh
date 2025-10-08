
set -e

source stop.sh
source lint.sh

# Start and wait to be ready
minikube start
minikube addons enable metrics-server
minikube kubectl -- wait pod --all --for=condition=Ready --namespace=kube-system --timeout=60s

# Upload into minikube, the load generator image
minikube image load load-generator:2025-10-08

source apply.sh
minikube kubectl -- wait pod --all --for=condition=Ready --namespace=case-study --timeout=120s

source current-state.sh

source curl.sh

kubectl logs -f -n case-study load-generator

#  USEFUL
#  kubectl logs -n case-study load-generator
#  kubectl exec -it  pod/load-generator -- sh

set -x

