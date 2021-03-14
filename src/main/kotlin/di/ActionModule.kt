package di

import dagger.Module
import dagger.Provides
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.flow.MutableStateFlow
import presentation.createscreen.CreateScreenState
import javax.inject.Singleton

@Module
abstract class ActionModule {

    companion object {

        @Provides
        @Singleton
        fun provideScope(): CoroutineScope = MainScope()

        @Provides
        @Singleton
        fun provideCreateScreenState(): MutableStateFlow<CreateScreenState> = MutableStateFlow(CreateScreenState())
    }
}