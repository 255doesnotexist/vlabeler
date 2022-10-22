package com.sdercolin.vlabeler.ui.dialog.plugin

import com.sdercolin.vlabeler.env.Log
import com.sdercolin.vlabeler.io.getSavedParamsFile
import com.sdercolin.vlabeler.model.BasePlugin
import com.sdercolin.vlabeler.model.LabelerConf
import com.sdercolin.vlabeler.model.Project
import com.sdercolin.vlabeler.ui.string.Strings
import com.sdercolin.vlabeler.ui.string.stringStatic
import com.sdercolin.vlabeler.util.ParamMap
import com.sdercolin.vlabeler.util.ParamTypedMap
import com.sdercolin.vlabeler.util.parseJson
import com.sdercolin.vlabeler.util.resolve
import com.sdercolin.vlabeler.util.stringifyJson

class LabelerDialogState(
    val labeler: LabelerConf,
    paramMap: ParamMap,
    override val savedParamMap: ParamMap?,
    override val submit: (ParamMap?) -> Unit,
    override val save: (ParamMap) -> Unit,
    override val load: (ParamMap) -> Unit,
    override val showSnackbar: suspend (String) -> Unit,
) : BasePluginDialogState(paramMap) {

    override val project: Project? = null

    override val basePlugin: BasePlugin
        get() = labeler

    override suspend fun import(target: BasePluginPresetTarget) = runCatching {
        when (target) {
            is BasePluginPresetTarget.File -> {
                val preset = target.file.readText().parseJson<BasePluginPreset>()
                if (preset.pluginName != labeler.name) {
                    throw IllegalArgumentException("Labeler name mismatch: ${preset.pluginName} != ${labeler.name}")
                }
                load(preset.params.resolve(labeler))
            }
            is BasePluginPresetTarget.Memory -> load(requireNotNull(target.item.preset).params.resolve(labeler))
        }
    }
        .onSuccess { showSnackbar(stringStatic(Strings.PluginDialogImportSuccess)) }
        .getOrElse {
            Log.error(it)
            showSnackbar(stringStatic(Strings.PluginDialogImportFailure))
        }

    override suspend fun export(params: ParamMap, target: BasePluginPresetTarget) = runCatching {
        when (target) {
            is BasePluginPresetTarget.File -> {
                val preset = BasePluginPreset(
                    pluginName = labeler.name,
                    pluginVersion = labeler.version,
                    params = ParamTypedMap.from(params, labeler.parameterDefs),
                )
                target.file.writeText(preset.stringifyJson())
            }
            is BasePluginPresetTarget.Memory -> {
                labeler.saveParams(params, labeler.getSavedParamsFile())
            }
        }
    }
        .onSuccess { showSnackbar(stringStatic(Strings.PluginDialogImportSuccess)) }
        .getOrElse {
            Log.error(it)
            showSnackbar(stringStatic(Strings.PluginDialogImportFailure))
        }
}
