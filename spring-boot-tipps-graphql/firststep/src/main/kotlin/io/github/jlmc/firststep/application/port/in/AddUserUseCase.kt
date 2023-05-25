package io.github.jlmc.firststep.application.port.`in`

interface AddUserUseCase {

    fun createUser(command: CreateUserCommand) : String
}
