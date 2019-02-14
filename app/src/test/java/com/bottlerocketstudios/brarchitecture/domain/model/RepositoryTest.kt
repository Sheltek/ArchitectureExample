package com.bottlerocketstudios.brarchitecture.domain.model

import com.bottlerocketstudios.brarchitecture.BaseTest
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RepositoryTest : BaseTest() {
    @Test
    fun repository_shouldHaveFields_whenConstructed() {
        val repository = Repository("scm", "name", true)
        assertThat(repository.scm).isEqualTo("scm")
        assertThat(repository.name).isEqualTo("name")
        assertThat(repository.is_private).isTrue()
    }
}