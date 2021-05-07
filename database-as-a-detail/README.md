# How to Make Your Database a Detail

This is an example application that is referenced in an upcoming blog post.

This application uses a Ports and Adapters style demonstrate how to keep persistence
details separate from your domain model, and towards the outside of your application.

### Requirements

* Postgres

## Setup

The application expects to be able to connect to a Postgres database
without a password as the `postgres` user.

```shell
$ createdb database-as-a-detail
```

## Running the application

```sh
$ ./gradlew dAAD:run
```

## Running the tests

```sh
$ ./gradlew dAAD:test
```
