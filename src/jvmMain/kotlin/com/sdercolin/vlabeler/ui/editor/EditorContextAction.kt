package com.sdercolin.vlabeler.ui.editor

import androidx.compose.foundation.ContextMenuItem
import androidx.compose.runtime.Composable
import com.sdercolin.vlabeler.ui.common.ContextMenuAction
import com.sdercolin.vlabeler.ui.string.*

sealed interface EditorContextAction : ContextMenuAction<EditorContextAction> {

    val text: Strings

    @Composable
    override fun toContextMenuItem(onClick: (EditorContextAction) -> Unit): ContextMenuItem {
        return ContextMenuItem(
            label = string(text),
            onClick = { onClick(this) },
        )
    }

    class OpenRenameEntryDialog(val entryIndex: Int) : EditorContextAction {
        override val text: Strings
            get() = Strings.EditorContextActionOpenRenameEntryDialog
    }

    class OpenDuplicateEntryDialog(val entryIndex: Int) : EditorContextAction {
        override val text: Strings
            get() = Strings.EditorContextActionOpenDuplicateEntryDialog
    }

    class OpenRemoveEntryDialog(val entryIndex: Int) : EditorContextAction {
        override val text: Strings
            get() = Strings.EditorContextActionOpenRemoveEntryDialog
    }

    class OpenMoveEntryDialog(val entryIndex: Int) : EditorContextAction {
        override val text: Strings
            get() = Strings.EditorContextActionOpenMoveEntryDialog
    }

    class CopyEntryName(val entryName: String) : EditorContextAction {
        override val text: Strings
            get() = Strings.EditorContextActionCopyEntryName
    }

    class CopySampleName(val sampleName: String) : EditorContextAction {
        override val text: Strings
            get() = Strings.EditorContextActionCopySampleName
    }

    class FilterBySampleName(val sampleName: String) : EditorContextAction {
        override val text: Strings
            get() = Strings.EditorContextActionFilterBySampleName
    }

    class FilterByTag(val tag: String) : EditorContextAction {
        override val text: Strings
            get() = Strings.EditorContextActionFilterByTag
    }

    class FilterStarred : EditorContextAction {
        override val text: Strings
            get() = Strings.EditorContextActionFilterStarred
    }

    class FilterUnstarred : EditorContextAction {
        override val text: Strings
            get() = Strings.EditorContextActionFilterUnstarred
    }

    class FilterDone : EditorContextAction {
        override val text: Strings
            get() = Strings.EditorContextActionFilterDone
    }

    class FilterUndone : EditorContextAction {
        override val text: Strings
            get() = Strings.EditorContextActionFilterUndone
    }
}
