package com.rndeep.fns_fantoo.ui.editor

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import android.provider.MediaStore
import android.provider.OpenableColumns
import android.view.*
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.content.res.AppCompatResources
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.flexbox.FlexboxItemDecoration
import com.google.android.material.snackbar.Snackbar
import com.google.common.net.MediaType
import com.rndeep.fns_fantoo.R
import com.rndeep.fns_fantoo.databinding.FragmentEditorBinding
import com.rndeep.fns_fantoo.utils.dismissKeyboard
import com.rndeep.fns_fantoo.utils.launchAndRepeatWithViewLifecycle
import com.rndeep.fns_fantoo.utils.showKeyboard
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber
import java.io.File


/**
 * Editor UI
 */
@AndroidEntryPoint
class EditorFragment : Fragment() {

    private lateinit var binding: FragmentEditorBinding
    private val viewModel: EditorViewModel by viewModels()
    private lateinit var hashtagAdapter: HashtagAdapter
    private lateinit var multimediaAdapter: MultimediaAdapter
    private lateinit var bottomSheet: EditorBottomSheet
    private val args: EditorFragmentArgs by navArgs()

    private val startForResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.apply {
                    Timber.d("image : ${result.data}")
                    viewModel.attachImage(this.data.toString(), requireContext())
                }
            }
        }

    private val startForVideoResult =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result: ActivityResult ->
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.apply {
                    Timber.d("video : ${result.data}")
                    viewModel.attachVideo(this.data.toString(), requireContext())
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Timber.d("info: ${args.editorInfo}")
        viewModel.setEditorInfo(args.editorInfo)
        viewModel.initBoardItems(args.editorInfo.editorType)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentEditorBinding.inflate(inflater, container, false).apply {
            toolbar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }

            multimediaAdapter = MultimediaAdapter { item ->
                Timber.d("delete item : $item")
                viewModel.deleteAttachedItem(item)
            }

            multimediaList.run {
                adapter = multimediaAdapter
            }

            hashtagAdapter = HashtagAdapter { hashtag ->
                viewModel.deleteHashtag(hashtag)
            }

            hashtagList.run {
                adapter = hashtagAdapter
                addItemDecoration(
                    FlexboxItemDecoration(requireContext()).apply {
                        setDrawable(
                            AppCompatResources.getDrawable(
                                requireContext(),
                                R.drawable.divider_empty_margin_small
                            )
                        )
                        setOrientation(FlexboxItemDecoration.VERTICAL)
                    }
                )
            }

            registerBtn.setOnClickListener {
                binding.loading.visibility = View.VISIBLE
                if (multimediaAdapter.itemCount > 0) {
                    viewModel.attachedItemFileList.forEachIndexed { index, multimediaFile ->
                        viewModel.sendCloudFlareData(
                            getString(R.string.cloudFlareKey),
                            multimediaFile.file,
                            multimediaFile.type,
                            index,
                            requireContext().getSharedPreferences("tusUpload",Activity.MODE_PRIVATE),
                            if(multimediaFile.type==MultimediaType.VIDEO)
                                File(getRealPathFromURI(Uri.parse(multimediaFile.fileUrl))?:"")
                            else
                                null
                        )
                    }
                } else {
                    viewModel.composePost()
                }
            }

            picture.setOnClickListener {
                showPhotoPicker()
            }

            video.setOnClickListener {
                showVideoPicker()
            }

            hashtag.setOnClickListener {
                hashtagInputContainer.visibility = View.VISIBLE
                showKeyboard(it)
                hashtagInput.requestFocus()
                viewModel.updateHashtagScroll(true)
            }

            completeBtn.setOnClickListener {
                hashtagInputContainer.visibility = View.GONE
                dismissKeyboard(it)
            }

            hashtagInput.doOnTextChanged { text, _, _, _ ->
                if (text.toString().endsWith(" ")) {
                    Timber.d("text.toString() 1 : ${text.toString()}")
                    if (text.toString().length > HASHTAG_MAX_LENGTH) {
                        showMessage(
                            hashtagInputContainer,
                            getString(R.string.se_h_can_writing_20_character_of_hashtag)
                        )
                        binding.hashtagInput.setText("")
                    }
                    if (viewModel.emojiRegex.containsMatchIn(text.toString())) {
                        showMessage(
                            hashtagInputContainer,
                            getString(R.string.se_h_exclude_sp_character_of_hashtag)
                        )
                    }
                    val hashtag = viewModel.emojiRegex.replace(text.toString(), "").replace(" ", "")
                    if (hashtag != "" && hashtag.length < HASHTAG_MAX_LENGTH + 1) {
                        viewModel.addHashtag(hashtag)
                    } else {
                        showMessage(
                            hashtagInputContainer,
                            getString(R.string.se_h_exclude_sp_character_of_hashtag)
                        )
                    }
                    binding.hashtagInput.setText("")
                }
            }

            anonymousSettingsSwitch.setOnCheckedChangeListener { _, b ->
                val msg =
                    if (b) getString(R.string.se_d_be_displaying_anonymous_writer) else getString(R.string.se_d_be_displaying_writer_nickname)

                showMessage(binding.attachItemContainer, msg)
                viewModel.setAnonymous(b)
            }
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {

        viewModel.attachedItem.observe(viewLifecycleOwner) { list ->
            Timber.d("attachedItem : ${list.size}")
            multimediaAdapter.submitList(list.map { it.copy() })

            viewModel.updateMultimediaScroll(true)
        }

        viewModel.hashtags.observe(viewLifecycleOwner) { list ->
            Timber.d("observe hashtags : $list")
            if (list.size > 10) {
                viewModel.deleteHashtag(list.last())
                showMessage(
                    binding.hashtagInputContainer,
                    getString(R.string.se_h_hashtag_can_write_maxium_10)
                )
            } else {
                hashtagAdapter.submitList(list.map { it })
            }

            viewModel.updateHashtagScroll(true)
        }

        binding.selectBoard.setOnClickListener {
            showEditorBottomSheet(viewModel.boardItems, BoardType.BOARD)
        }

        binding.selectSubject.setOnClickListener {
            showEditorBottomSheet(viewModel.subjectItems, BoardType.SUBJECT)
        }

        binding.inputTitle.doOnTextChanged { text, _, _, _ ->
            viewModel.setTitle(text.toString())
        }

        binding.inputContent.doOnTextChanged { text, _, _, _ ->
            viewModel.setContent(text.toString())
            if (text.toString().endsWith("\n")) {
                val count = text.toString().filter { it == '\n' }.length
                if (count > viewModel.enterCnt) {
                    Timber.d("doOnTextChanged, count: $count, viewModel enterCnt: ${viewModel.enterCnt}")
                    viewModel.updateTextScroll(true)
                    viewModel.enterCnt = count
                }
            }
        }

        binding.noticeRegisterCheck.setOnClickListener {
            val checked = viewModel.isNoticeRegisterChecked.value
            checked?.let {
                viewModel.setIsNoticeRegisterChecked(!it)
            }
        }

        binding.topFixCheck.setOnClickListener {
            val checked = viewModel.isTopFixedChecked.value
            checked?.let {
                viewModel.setIsTopFixedChecked(!it)
            }
        }

        args.editorInfo.clubMemberLevel?.let { level ->
            if (level == CLUB_OWNER_LEVEL) {
                binding.noticeRegisterCheck.visibility = View.VISIBLE
                binding.noticeRegister.visibility = View.VISIBLE
                binding.topFix.visibility = View.VISIBLE
                binding.topFixCheck.visibility = View.VISIBLE
            }
        }

        if (args.editorInfo.editorType == EditorType.CLUB) {
            binding.anonymousSettingsSwitch.visibility = View.GONE
        }

        viewModel.isNoticeRegisterChecked.observe(viewLifecycleOwner) { checked ->
            setNoticeRegisterCheck(checked)
        }

        viewModel.isTopFixedChecked.observe(viewLifecycleOwner) { checked ->
            setTopFixedCheck(checked)
        }

        viewModel.textScroll.observe(viewLifecycleOwner) {
            val height = binding.multimediaList.top - binding.inputContent.bottom + 34 + 73 * 2
            Timber.d("inputContent.bottom: ${binding.inputContent.bottom}, height: $height")
            binding.scroll.smoothScrollTo(0, binding.inputContent.bottom - height)
        }

        viewModel.multimediaScroll.observe(viewLifecycleOwner) {
            Timber.d("observe multimediaScroll : $it")
            binding.scroll.smoothScrollTo(0, binding.multimediaList.bottom)
        }

        viewModel.hashtagScroll.observe(viewLifecycleOwner) {
            Timber.d("observe hashtagScroll : $it")
            binding.scroll.smoothScrollTo(0, binding.hashtagList.bottom)
        }

        viewModel.cloudFlareSendResult.observe(viewLifecycleOwner) { cloudItem ->
            cloudItem.forEach {
                if (!it.success) {
                    Timber.d("CloudFlare UploadFail Index => ${it.index}")
                    return@forEach
                }
                Timber.d("CloudFlare Upload ID => ${it.cloudFlareID} | Type => ${it.type}")
            }
            viewModel.composePost()
        }

        viewModel.isComplete.observe(viewLifecycleOwner) { msg ->
            binding.loading.visibility = View.GONE
            if (msg != null) {
                showMessage(binding.attachItemContainer, msg)
            } else {
                view.findNavController().navigateUp()
            }
        }

        viewModel.showSubject.observe(viewLifecycleOwner) {
            if(it) {
                showSubjectBoard()
            }
        }

        launchAndRepeatWithViewLifecycle {
            viewModel.isRegisterBtnEnabled.collect { enabled ->
                val color = when (enabled) {
                    true -> requireContext().getColor(R.color.primary_default)
                    else -> requireContext().getColor(R.color.btn_inactive_gray)
                }
                binding.registerBtn.setTextColor(color)
                binding.registerBtn.isEnabled = enabled
            }
        }
    }

    private fun showPhotoPicker() {
        val intent = Intent().apply {
            type = "image/*"
            action = Intent.ACTION_PICK
            putExtra(Intent.EXTRA_MIME_TYPES, arrayOf("image/*"))
        }
        startForResult.launch(Intent.createChooser(intent, "Get Album"))
    }

    private fun showVideoPicker() {
        val intent = Intent().apply {
            type = "video/*"
            action = Intent.ACTION_GET_CONTENT
        }
        startForVideoResult.launch(Intent.createChooser(intent, "Get Album"))
    }

    private fun showEditorBottomSheet(boardItems: List<BoardItem>, boardType: BoardType) {
        bottomSheet = EditorBottomSheet(boardItems) { item ->
            Timber.d("item : $item")
            viewModel.updateBoardItem(BoardItem(item.code, item.name, true), boardType)
            if (boardType == BoardType.BOARD) {
                binding.selectBoard.text = item.name
                viewModel.initSubjectBoardItems(item.code)
            } else {
                binding.selectSubject.text = item.name
            }
            bottomSheet.dismiss()
        }
        bottomSheet.show(requireActivity().supportFragmentManager, EditorBottomSheet.TAG)
    }

    private fun showSubjectBoard() {
        binding.selectSubject.text = requireContext().getText(R.string.m_summury_select)
        binding.selectSubject.visibility = View.VISIBLE
    }

    private fun showMessage(view: View, msg: String) {
        val snackbar = Snackbar.make(view, msg, Snackbar.LENGTH_SHORT)

        val params = snackbar.view.layoutParams as CoordinatorLayout.LayoutParams
        params.gravity = Gravity.BOTTOM or Gravity.CENTER_HORIZONTAL
        params.width = CoordinatorLayout.LayoutParams.WRAP_CONTENT
        snackbar.view.layoutParams = params

        snackbar
            .setBackgroundTint(requireContext().getColor(R.color.gray_870_opacity80))
            .setAnchorView(view)
            .show()
    }

    private fun setNoticeRegisterCheck(checked: Boolean) {
        if(checked) {
            binding.noticeRegisterCheck.setImageResource(R.drawable.checkbox_round_check)
            binding.selectBoard.isClickable = false
            binding.selectSubject.isClickable = false
            val color = requireContext().getColor(R.color.state_enable_gray_400)
            binding.selectBoard.setTextColor(color)
            binding.selectBoard.text = requireContext().getString(R.string.g_board_select)
            binding.selectSubject.text = requireContext().getString(R.string.m_summury_select)
            binding.selectSubject.visibility = View.GONE
            binding.selectSubject.setTextColor(color)
        } else {

            binding.noticeRegisterCheck.setImageResource(R.drawable.checkbox_round_uncheck)
            binding.selectBoard.isClickable = true
            binding.selectSubject.isClickable = true
            val color = requireContext().getColor(R.color.state_enable_gray_900)
            binding.selectBoard.setTextColor(color)
            binding.selectSubject.setTextColor(color)
        }
    }

    private fun setTopFixedCheck(checked: Boolean) {
        if(checked) {
            binding.topFixCheck.setImageResource(R.drawable.checkbox_round_check)
        } else {
            binding.topFixCheck.setImageResource(R.drawable.checkbox_round_uncheck)
        }
    }

    private fun getRealPathFromURI(contentUri:Uri) :String? {
        if (contentUri.path?.startsWith("/storage")==true) {
            return contentUri.path
        }

        val id = DocumentsContract.getDocumentId(contentUri).split(":")[1]
        val columns = arrayOf(MediaStore.Files.FileColumns.DATA)
        val selection = MediaStore.Files.FileColumns._ID + " = " + id
        val cursor = requireContext().contentResolver.query(MediaStore.Files.getContentUri("external"), columns, selection, null, null)
        try {
            val columnIndex = cursor?.getColumnIndex(columns[0])
            if (cursor?.moveToFirst()==true) {
                return cursor.getString(columnIndex?:0)
            }
        } finally {
            cursor?.close();
        }
        return null;
    }

    private fun queryName(context: Context, uri: Uri): String? {
        val returnCursor: Cursor = context.contentResolver.query(uri, null, null, null, null)!!
        val nameIndex: Int = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name: String = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }

    companion object {
        const val CLUB_OWNER_LEVEL = 20
        const val HASHTAG_MAX_LENGTH = 20
    }

}