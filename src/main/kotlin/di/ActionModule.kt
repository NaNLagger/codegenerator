package di

import dagger.Module
import dagger.Provides
import io.reactivex.subjects.BehaviorSubject
import org.apache.velocity.app.VelocityEngine
import org.apache.velocity.runtime.log.NullLogChute
import presentation.createscreen.CreateScreenState
import javax.inject.Singleton

@Module
abstract class ActionModule {

    companion object {

        @Provides
        @Singleton
        fun provideCreateScreenState(): BehaviorSubject<CreateScreenState> = BehaviorSubject.create()

        @Provides
        @Singleton
        fun provideVelocityEngine(): VelocityEngine {
            val velocityEngine = VelocityEngine()
            velocityEngine.setProperty("runtime.log.logsystem.class", NullLogChute::class.java.name)
            velocityEngine.init()
            return velocityEngine
        }
    }
}