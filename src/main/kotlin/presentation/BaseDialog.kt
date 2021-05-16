package presentation

import com.intellij.openapi.ui.DialogWrapper
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import javax.swing.JComponent
import javax.swing.JPanel

abstract class BaseDialog<View : JComponent> : DialogWrapper(true) {

    private val lifecycleDisposable: CompositeDisposable = CompositeDisposable()
    protected lateinit var view: View

    protected abstract fun createView(): View

    override fun createCenterPanel(): JComponent {
        return createView().also {
            view = it
            onViewCreated()
            contentPanel
        }
    }

    abstract fun onViewCreated()

    override fun dispose() {
        super.dispose()
        lifecycleDisposable.dispose()
    }

    protected fun Disposable.addLifecycle() {
        lifecycleDisposable.add(this)
    }
}