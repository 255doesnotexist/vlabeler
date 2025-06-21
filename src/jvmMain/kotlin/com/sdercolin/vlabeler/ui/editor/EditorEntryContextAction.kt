package com.sdercolin.vlabeler.ui.editor

import androidx.compose.foundation.ContextMenuItem
import androidx.compose.runtime.Composable
import com.sdercolin.vlabeler.ui.common.ContextMenuAction
import com.sdercolin.vlabeler.ui.string.*

sealed interface EditorEntryContextAction : ContextMenuAction<EditorEntryContextAction> {

    val text: Strings

    @Composable
    override fun toContextMenuItem(onClick: (EditorEntryContextAction) -> Unit): ContextMenuItem {
        return ContextMenuItem(
            label = string(text),
            onClick = { onClick(this) },
        )
    }

    class OpenRenameEntryDialog(val entryIndex: Int) : EditorEntryContextAction {
        override val text: Strings
            get() = Strings.EditorContextActionOpenRenameEntryDialog
    }

    class OpenDuplicateEntryDialog(val entryIndex: Int) : EditorEntryContextAction {
        override val text: Strings
            get() = Strings.EditorContextActionOpenDuplicateEntryDialog
    }

    class OpenRemoveEntryDialog(val entryIndex: Int) : EditorEntryContextAction {
        override val text: Strings
            get() = Strings.EditorContextActionOpenRemoveEntryDialog
    }

    class OpenMoveEntryDialog(val entryIndex: Int) : EditorEntryContextAction {
        override val text: Strings
            get() = Strings.EditorContextActionOpenMoveEntryDialog
    }

    class CopyEntryName(val entryName: String) : EditorEntryContextAction {
        override val text: Strings
            get() = Strings.EditorContextActionCopyEntryName
    }

    class FilterByEntryName(val entryName: String) : EditorEntryContextAction {
        override val text: Strings
            get() = Strings.EditorContextActionFilterByEntryName
    }

    class CopySampleName(val sampleName: String) : EditorEntryContextAction {
        override val text: Strings
            get() = Strings.EditorContextActionCopySampleName
    }

    class FilterBySampleName(val sampleName: String) : EditorEntryContextAction {
        override val text: Strings
            get() = Strings.EditorContextActionFilterBySampleName
    }

    class FilterByTag(val tag: String) : EditorEntryContextAction {
        override val text: Strings
            get() = Strings.EditorContextActionFilterByTag
    }

    class FilterStarred : EditorEntryContextAction {
        override val text: Strings
            get() = Strings.EditorContextActionFilterStarred
    }

    class FilterUnstarred : EditorEntryContextAction {
        override val text: Strings
            get() = Strings.EditorContextActionFilterUnstarred
    }

    class FilterDone : EditorEntryContextAction {
        override val text: Strings
            get() = Strings.EditorContextActionFilterDone
    }

    class FilterUndone : EditorEntryContextAction {
        override val text: Strings
            get() = Strings.EditorContextActionFilterUndone
    }
}
