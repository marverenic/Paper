package com.marverenic.reader.data

import android.util.Log
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.disposables.Disposable
import io.reactivex.subjects.BehaviorSubject

class RxLoader<T>(
        default: Single<T>? = null,
        recomputeDefault: Single<Boolean> = Single.just(false),
        private val load: () -> Single<T>) {

    private val subject: BehaviorSubject<T> = BehaviorSubject.create()

    private val isLoading: BehaviorSubject<Boolean> = BehaviorSubject.createDefault(false)

    private var workerDisposable: Disposable? = null

    init {
        default?.let {
            isLoading.onNext(true)
            workerDisposable = it.subscribe({ defaultValue ->
                setValue(defaultValue)
                recomputeDefault.subscribe { recompute ->
                    if (recompute) {
                        computeValue()
                    }
                }
            }, { t ->
                Log.e("RxLoader", "Failed to load default value", t)
                isLoading.onNext(false)
                computeValue()
            })
        }
    }

    fun computeValue(): Observable<T> {
        isLoading.take(1).subscribe { loading ->
            if (!loading) {
                isLoading.onNext(true)
                workerDisposable = load()
                        .doOnEvent { _, _ -> isLoading.onNext(false) }
                        .subscribe(this::setValue)
            }
        }
        return subject
    }

    fun getOrComputeValue(): Observable<T> {
        return if (!subject.hasValue()) computeValue()
        else subject
    }

    fun isComputingValue(): Observable<Boolean> = isLoading

    fun getValue(): T? = if (subject.hasValue()) subject.value else null

    fun setValue(t: T) {
        workerDisposable?.dispose()
        workerDisposable = null

        subject.onNext(t)
        isLoading.onNext(false)
    }

    fun getObservable(): Observable<T> = subject

}