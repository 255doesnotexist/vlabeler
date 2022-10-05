package com.sdercolin.vlabeler.ipc

import com.sdercolin.vlabeler.env.Log
import com.sdercolin.vlabeler.ipc.request.IpcRequest
import com.sdercolin.vlabeler.ipc.response.IpcResponse
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.launch
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import org.zeromq.SocketType
import org.zeromq.ZContext
import org.zeromq.ZMQ
import org.zeromq.ZMQException
import zmq.ZError

class IpcServer(val coroutineScope: CoroutineScope) {

    private val zContext = ZContext()
    private lateinit var socket: ZMQ.Socket
    private var job: Job? = null

    fun bind() {
        socket = zContext.createSocket(SocketType.REP)
        try {
            socket.bind("tcp://*:$Port")
            Log.debug("IpcServer bind to port $Port")
        } catch (t: Throwable) {
            Log.error("Failed to bind socket:")
            Log.error(t)
        }
    }

    fun startReceive(requestFlow: MutableSharedFlow<IpcRequest>) {
        job?.cancel()
        job = coroutineScope.launch(Dispatchers.IO) {
            while (job?.isActive == true && zContext.isClosed.not()) {
                try {
                    delay(ThrottlePeriodMs)
                    val message = socket.recvStr()
                    Log.info("Received request: $message")
                    val request = jsonForIpc.decodeFromString<IpcRequest>(message)
                    requestFlow.emit(request)
                } catch (t: Throwable) {
                    if (t !is CancellationException && (t as? ZMQException)?.errorCode != ZError.ETERM) {
                        Log.error("Failed to receive an IPC request:")
                        Log.error(t)
                    }
                }
            }
        }
    }

    fun send(response: IpcResponse) {
        try {
            socket.send(jsonForIpc.encodeToString(response))
        } catch (t: Throwable) {
            Log.error("Failed to send an IPC response:")
            Log.error(t)
        }
    }

    fun close() {
        job?.cancel()
        zContext.destroy()
        Log.debug("IpcServer closed")
    }

    companion object {
        private const val ThrottlePeriodMs = 200L
        private const val Port = 32342
    }
}
