package com.bottlerocketstudios.brarchitecture.data.model

import com.bottlerocketstudios.brarchitecture.data.environment.EnvironmentType

/** Contains all environment specific data. Globally unique data should live outside of this config. */
data class EnvironmentConfig(
    val environmentType: EnvironmentType,
    /** Base url for the environment */
    val baseUrl: String
) : DomainModel
