---
title: HTTP Interface
description: Describes on how the HTTP service works with Kanata

---

Kanata has a HTTP overlay using [rocket](https://rocket.rs), it have a RESTful API and an administration dashboard for authorized users to access
and check on the state of Kanata, and this is a reference sheet on the RESTful API.

## Authentication
Authentication is not usually required to query pods that Kanata is managing, but for the administration dashboard, you will have to authenticate
with a email and password defined in **/opt/noel/kanata/users.yml**:

```yml
users:
  - <username>: <password>
```

To generate a password, you can run the `create-user` command with the **kanata** binary to add it to `users.yml`:

```sh
$ kanata create-user <user> <password>
```

> {{warn}} (If `user` and/or `password` are not added, it will be prompted.) {{/warn}}

Now, you can login at `http://localhost:2934/admin` to go in and view it!

## Routes
All routes will be prefixed with `/{version}` depending on version changes and releases.

| Version | Available? |
|---------|------------|
| `v1`    | Yes        |

### %{path: /, method: GET}
> Returns the base endpoint, which will return a JSON object.

#### Responses
##### 200

```json
{
   "hello": "world",
   "version": "v{version} (commit: {commit}, build date: {build-date})"
}
```

### %{path: /pods, method: GET}
> Returns the list of the in-memory cached pods available.

#### Responses
##### 503
> If the server is still bootstrapping the Kubernetes service.

##### 200
> Returns a JSON array of a [Pod](/types/pod) available.

### %{path: /pods/watch, method: GET}
> Creates a stream of the pods watching, read more on the [Watching](/watching) guide on how to.
