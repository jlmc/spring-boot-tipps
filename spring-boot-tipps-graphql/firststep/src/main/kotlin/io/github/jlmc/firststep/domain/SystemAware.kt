package io.github.jlmc.firststep.domain

import jakarta.persistence.Column
import jakarta.persistence.Embeddable

@Embeddable
class SystemAware {
    @Column(name = "system")
    var system: String? = null

    constructor()
    private constructor(system: String) {
        this.system = system
    }

    companion object {
        @JvmStatic
        fun createSystemAware(system: String): SystemAware {
            return SystemAware(system)
        }
    }
}
