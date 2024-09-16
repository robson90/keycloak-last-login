[![CI](https://github.com/robson90/keycloak-last-login/workflows/Maven%20CI%2FCD/badge.svg)](https://github.com/robson90/keycloak-last-login/actions?query=workflow%3AMaven)
[![GitHub release (latest SemVer)](https://img.shields.io/github/v/release/robson90/keycloak-last-login?logo=github&sort=semver)](https://github.com/robson90/keycloak-last-login/releases/latest)

# Keycloak: Last Login

## What will this extension do ?

After you have setup the Custom Event, this Extension will add an attribute(key is configurable - value = Timestamp) to every user, once he logs in. On another login, the attribute gets updated.

## What will be coming ?

* An API, that will return all users, that have its lastLogin before a specified parameter
* A second Parameter, that is human readable