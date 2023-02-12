## Edomata example

This project is from examples in the tutorial, which models two domains, and uses two different runtimes (cats effect and ZIO).

## Run
Start postgres:
``` sh
docker-compose up
```

In a different shell:

``` sh
sbt catsEffectJVM/run
```

or for ZIO example:

``` sh
sbt zio/run
```


Visit https://edomata.ir/ for tutorial and more info.
