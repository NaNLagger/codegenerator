package di

import dagger.Module
import dagger.Provides
import io.reactivex.subjects.BehaviorSubject
import presentation.createscreen.CreateScreenState
import javax.inject.Singleton

@Module
abstract class ActionModule {

    companion object {

        @Provides
        @Singleton
        fun provideCreateScreenState(): BehaviorSubject<CreateScreenState> = BehaviorSubject.create<CreateScreenState>()
    }
}