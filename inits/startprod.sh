#!/usr/bin/env bash

# Delete old PID file if exists
[ -f bin/RUNNING_PID ] && rm bin/RUNNING_PID

it4all-0.9.0/bin/it4all -Dplay.http.secret.key="$(date | md5sum)" -Dplay.evolutions.db.default.autoApply=true
