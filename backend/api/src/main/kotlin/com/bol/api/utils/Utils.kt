package com.bol.api.utils

import java.util.UUID

fun shortUuid(uuid: UUID): String = uuid.toString().substring(0, 9)