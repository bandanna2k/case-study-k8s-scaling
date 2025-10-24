#echo '----------------- JOBS -----------------'
#kubectl get jobs -A
#echo

echo '----------------- PODS -----------------'
kubectl get pods -A
echo

echo '----------------- SERVICES -----------------'
kubectl get services -A
echo

echo '----------------- DEPLOYMENTS -----------------'
kubectl get deployments -A
echo

echo '----------------- REPLICA SETS -----------------'
kubectl get rs -A
echo

echo '----------------- HORIZONTAL POD AUTOSCALERS -----------------'
kubectl get hpa -A

echo '----------------- TOP PODS -----------------'
kubectl top pods -n case-study

#echo '----------------- SCALED OBJECTS -----------------'
#kubectl get scaledobjects -A

