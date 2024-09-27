package me.ijachok.owtesttask

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.view.ViewGroup
import android.webkit.CookieManager
import android.webkit.ValueCallback
import android.webkit.WebChromeClient
import android.webkit.WebResourceRequest
import android.webkit.WebSettings
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.VisibilityThreshold
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.BasicAlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import dagger.hilt.android.AndroidEntryPoint
import me.ijachok.owtesttask.model.ContentType
import me.ijachok.owtesttask.ui.theme.OWTestTaskTheme

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private var upload: ValueCallback<Array<Uri>>? = null
    private var photoUri: Uri? = null
    private lateinit var sharedPreferences: SharedPreferences

    private val launcherGallery = registerForActivityResult(
        PickVisualMedia()
    ) {

        if (it != null && upload != null) upload!!.onReceiveValue(arrayOf(it))

        upload = null
    }

    private val launcherCamera = registerForActivityResult(
        StartActivityForResult()
    ) {
        if (photoUri != null && it.resultCode == RESULT_OK) {
            if (upload != null) upload!!.onReceiveValue(arrayOf(photoUri!!))

        }

        upload = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        sharedPreferences = getSharedPreferences("sp", MODE_PRIVATE)

        enableEdgeToEdge()
        setContent {
            val mainViewModel = hiltViewModel<MainViewModel>()
            val contentType by mainViewModel.contentType.collectAsState()
            val isLoading by mainViewModel.isLoading.collectAsState()
            val isNavigating by mainViewModel.isNavigating.collectAsState()
            OWTestTaskTheme {
                Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                    Column(
                        Modifier
                            .padding(innerPadding)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.SpaceEvenly
                    ) {
                        if (isLoading) {
                            CircularProgressIndicator()
                        } else {
                            AnimatedContent(
                                modifier = Modifier
                                    .padding(horizontal = 16.dp)
                                    .fillMaxWidth()
                                    .aspectRatio(0.8f)
                                    .clip(RoundedCornerShape(4.dp)),
                                transitionSpec = {
                                    slideInHorizontally(
                                        spring(
                                            stiffness = Spring.StiffnessMedium,
                                            visibilityThreshold = IntOffset.VisibilityThreshold
                                        ),
                                        initialOffsetX = { it / 4 }
                                    ) + fadeIn() togetherWith
                                            slideOutHorizontally(
                                                spring(
                                                    stiffness = Spring.StiffnessMedium,
                                                    visibilityThreshold = IntOffset.VisibilityThreshold
                                                ),
                                                targetOffsetX = { -it / 4 }) + fadeOut()
                                },
                                contentAlignment = Alignment.Center,
                                targetState = contentType
                            ) { targetType ->
                                when (targetType) {
                                    ContentType.TEXT -> {
                                        Box(
                                            Modifier
                                                .fillMaxSize()
                                                .background(MaterialTheme.colorScheme.surfaceContainer),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                "jGWG KVwgd VWkg VkEWG v Gev GveGH v A e efSe Se fS efE fasE fs ef s",
                                                textAlign = TextAlign.Center
                                            )
                                        }
                                    }

                                    ContentType.WEB_VIEW -> {
                                        MyWebView(Modifier.fillMaxSize())
                                    }

                                    else -> {
                                        Image(
                                            painterResource(R.drawable.ic_launcher_background),
                                            contentDescription = null,
                                            modifier = Modifier.fillMaxSize(),
                                            contentScale = ContentScale.Crop
                                        )
                                    }

                                }
                            }

                            Button(onClick = { mainViewModel.nextType() }) { Text("Далее") }
                            Box(Modifier.size(50.dp)) {
                                if (isNavigating) CircularProgressIndicator(
                                    Modifier.size(50.dp)
                                )
                            }

                        }
                    }
                }
            }
        }
    }

    override fun onPause() {
        super.onPause()
        CookieManager.getInstance().flush()
    }

    override fun onResume() {
        super.onResume()
        CookieManager.getInstance().flush()
    }

    @SuppressLint("SetJavaScriptEnabled")
    @Composable
    fun MyWebView(modifier: Modifier = Modifier) {
        var isShowDialog by remember { mutableStateOf(false) }
        val context = LocalContext.current
        val webView = remember {
            WebView(context).apply {
                val cookieManager: CookieManager = CookieManager.getInstance()
                cookieManager.setAcceptCookie(true)
                cookieManager.acceptCookie()
                cookieManager.setAcceptThirdPartyCookies(this, true)
                cookieManager.flush()

                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )

                settings.apply {
                    userAgentString = this.userAgentString
                    javaScriptEnabled = true
                    cacheMode = WebSettings.LOAD_NO_CACHE
                    domStorageEnabled = true
                    databaseEnabled = true
                    setSupportZoom(false)
                    allowFileAccess = true
                    allowContentAccess = true
                    loadWithOverviewMode = true
                    useWideViewPort = true
                    javaScriptCanOpenWindowsAutomatically = true
                }
                scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
                requestFocus(View.FOCUS_DOWN)
                setLayerType(View.LAYER_TYPE_HARDWARE, null)
                loadUrl("https://postimages.org/")
                scrollBarStyle = WebView.SCROLLBARS_OUTSIDE_OVERLAY
                this.webViewClient = object : WebViewClient() {
                    override fun shouldOverrideUrlLoading(
                        view: WebView?,
                        request: WebResourceRequest
                    ): Boolean {
                        return rewriteLink(view!!, request.url.toString())
                    }

                    private fun rewriteLink(view: WebView, url: String): Boolean {
                        val isBot = try {
                            val botParam = Uri.parse(url).getQueryParameter("bot")
                            botParam?.toBoolean() ?: false
                        } catch (_: Exception) {
                            false
                        }
                        return if (isBot) {
                            sharedPreferences.edit()
                                .putString(getString(R.string.saved_link_key), "").apply()

                            //open game and finish webview
//                            startActivity(
//                                Intent(
//                                    this@MainActivity,
//                                    GameActivity::class.java
//                                )
//                            )
//                            finish()

                            true
                        } else if (url.startsWith("mailto:")) {
                            startActivity(Intent(Intent.ACTION_SENDTO, Uri.parse(url)))
                            true
                        } else if (url.startsWith("tg:") ||
                            url.startsWith("https://t.me") ||
                            url.startsWith("https://telegram.me")
                        ) {
                            try {
                                startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(view.hitTestResult.extra)
                                    )
                                )
                            } catch (_: Exception) {
                            }
                            true
                        } else if (url.startsWith("https://offer-wall.com") || url.startsWith("offer-wall.net")) {
                            try {
                                startActivity(
                                    Intent(
                                        Intent.ACTION_VIEW,
                                        Uri.parse(url)
                                    )
                                )
                            } catch (_: Exception) {
                            }
                            true
                        } else {
                            false
                        }
                    }

                    override fun onPageFinished(view: WebView?, url: String?) {
                        super.onPageFinished(view, url)
                        if (url != null) { //save link if loading successful
                            sharedPreferences.edit()
                                .putString(getString(R.string.saved_link_key), url).apply()
                        }
                        CookieManager.getInstance().flush()
                    }

                    override fun onPageStarted(view: WebView?, url: String?, favicon: Bitmap?) {
                        super.onPageStarted(view, url, favicon)
                    }
                }
                webChromeClient = object : WebChromeClient() {
                    override fun onShowFileChooser(
                        webView: WebView?,
                        filePathCallback: ValueCallback<Array<Uri>>?,
                        fileChooserParams: FileChooserParams?
                    ): Boolean {

                        upload?.onReceiveValue(null)
                        upload = filePathCallback

                        isShowDialog = true
                        return true
                    }
                }
            }
        }
        BackHandler {
            if (webView.canGoBack()) {
                webView.goBack()
            } else {
                finish()
            }
        }

        AndroidView(modifier = modifier, factory = { webView })

        if (isShowDialog) UploadDialog { isShowDialog = false }

    }

    @Preview
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun UploadDialog(modifier: Modifier = Modifier, onCancel: () -> Unit = {}) {
        BasicAlertDialog(
            onDismissRequest = { onCancel() },
            modifier = modifier.clip(RoundedCornerShape(16.dp))
        ) {
            Column(
                Modifier
                    .background(MaterialTheme.colorScheme.surfaceContainer)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceEvenly) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "",
                        Modifier
                            .size(60.dp)
                            .clickable {
                                if (ContextCompat.checkSelfPermission(
                                        this@MainActivity,
                                        android.Manifest.permission.CAMERA
                                    ) == PackageManager.PERMISSION_GRANTED
                                ) {
                                    onCancel()
                                    val values = ContentValues()
                                    values.put(MediaStore.Images.Media.TITLE, "New Picture")
                                    values.put(
                                        MediaStore.Images.Media.DESCRIPTION,
                                        "From Camera"
                                    )
                                    photoUri = contentResolver.insert(
                                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                                        values
                                    )
                                    val takePictureIntent =
                                        Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                                    takePictureIntent.putExtra(
                                        MediaStore.EXTRA_OUTPUT,
                                        photoUri
                                    )
                                    launcherCamera.launch(takePictureIntent)
                                } else {
                                    Toast
                                        .makeText(
                                            this@MainActivity,
                                            "Please allow camera usage",
                                            Toast.LENGTH_SHORT
                                        )
                                        .show()
                                    ActivityCompat.requestPermissions(
                                        this@MainActivity,
                                        arrayOf(android.Manifest.permission.CAMERA),
                                        100
                                    )

                                }

                            }
                    )
                    Icon(
                        imageVector = Icons.Default.Image,
                        contentDescription = "",
                        Modifier
                            .size(60.dp)
                            .clickable {
                                onCancel()

                                launcherGallery.launch(PickVisualMediaRequest(PickVisualMedia.ImageOnly))
                            }
                    )
                }

                Spacer(Modifier.height(16.dp))

                Button(onClick = { onCancel() }) {
                    Text(text = "Cancel")
                }

            }


        }
    }
}

