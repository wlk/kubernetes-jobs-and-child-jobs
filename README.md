Scope of the project:
* Ability to start k8s jobs via client
* For now on local minikube
* Top level job can start child jobs
* And monitor their completion state


#Starting k8s Jobs 101

There are 2 ways k8s can be started, I'm describing both approaches because at the moment it's not clear which one is preferable:

### Starting jobs via yaml (Kubernetes objects)

You define a yaml file like this:
```
apiVersion: batch/v1
kind: Job
metadata:
  name: pi
spec:
  template:
    metadata:
      name: pi
    spec:
      containers:
      - name: pi
        image: perl
        command: ["perl",  "-Mbignum=bpi", "-wle", "print bpi(8000)"]
      restartPolicy: Never
```
Save it as `job.yaml`

and then run 
```
kubectl apply -f job.yaml
```

This will create a defined job and start it right away.

After completion it's usually good idea to cleanup, to do this you need to run:
```
kubectl delete -f job.yaml
```

### Starting jobs via commandline
Example:
```
kubectl run pi --image=perl --restart=OnFailure -- perl -Mbignum=bpi -wle 'print bpi(2000)'
```

This is technically not a real k8s job, but rater it starts a pod and stops when the command passed as a parameter completes.
BTW. The same pattern can be used to start real services, more information: `kubectl run -h`
 
## Restarting jobs
There is no way to manually restart a job, so for example if I create job defined via yaml, there is no way to reuse that definition
So for our use case it's not possible to create a parametrized job that can be triggered to start periodically with a set of parameters

