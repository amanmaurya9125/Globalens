package com.example.Globalens

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.rememberAsyncImagePainter
import com.example.Globalens.Theme.ThemeMode
import com.example.Globalens.Theme.Theme_Preferences
import com.example.Globalens.ViewModel.NewsViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun ProfileScreen(navController: NavController, viewModel: NewsViewModel,themeFlow: ThemeMode) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val sharePref = SharedPreferences()
    val userPref = SharedPreferences().getUserAuth(context)
    var userauth = remember { mutableStateOf(false) }
    val userName = userPref["name"]
    val imageUrl = userPref["photo"]
    if (userPref["uid"] != null) {
        userauth.value = true
    }
    val themeList = listOf(
        R.drawable.brightness to ThemeMode.Default,
        R.drawable.summer to ThemeMode.Light,
        R.drawable.night_mode to ThemeMode.Dark
    )
    BackHandler{
        scope.launch {
            navController.navigate("Home"){
                launchSingleTop = true
            }
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 25.dp, start = 20.dp, end = 20.dp, bottom = 20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (userauth.value) {
                ProfileTop_After(userName.toString(), imageUrl.toString())
            } else {
                ProfileTop_Before(navController)
            }
        }
        HorizontalDivider(modifier = Modifier.padding(vertical = 5.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxWidth().height(80.dp)
                .padding(top = 5.dp, end = 5.dp, start = 5.dp, bottom = 5.dp)                .clickable {
                    navController.navigate("savedArticle")
                },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                painter = painterResource(id = R.drawable.unsaved),
                contentDescription = "Unsaved",
                modifier = Modifier
                    .padding(horizontal = 5.dp)
                    .size(35.dp)
            )
            Text(
                "Saved Articles",
                style = MaterialTheme.typography.headlineSmall
            )

        }

        HorizontalDivider(modifier = Modifier.padding(vertical = 5.dp))
        // THEME

        Column(
            modifier = Modifier
                .padding(vertical = 10.dp, horizontal = 7.dp)
                .fillMaxWidth()
                .background(
                    shape = RoundedCornerShape(25.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant
                )
        ) {
            LazyRow(
                modifier = Modifier
                    .padding(10.dp)
                    .fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                items(themeList) { (image, mode) ->
                    Column(
                        modifier = Modifier.fillParentMaxWidth(1f / themeList.size).clickable {
                            scope.launch{
                                Theme_Preferences.saveTheme(context,mode)
                            }
                        }.padding(top = 12.dp, bottom = 7.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        val isSelected = mode == themeFlow

                        Image(
                            painter = painterResource(image), null,
                            modifier = Modifier
                                .size(60.dp)
                                .border(
                                    width = if (isSelected) 3.dp else 0.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.onSurfaceVariant else Color.Transparent,
                                    shape = RoundedCornerShape(12)
                                )


                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "${mode}",
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    }
                }
            }
        }
        HorizontalDivider(Modifier.padding(top = 7.dp))

//SendFeedBack
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth().height(80.dp)
                .padding(top = 5.dp, end = 5.dp, start = 5.dp, bottom = 5.dp)
                .clickable {
                   sendFeedBack(context)
                }
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Send Feedback",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    painter = painterResource(id = R.drawable.black_arrow),
                    contentDescription = "Black_Arrow",
                    modifier = Modifier.padding(horizontal = 10.dp)
                )

            }
        }

        HorizontalDivider()
//            Logout
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxWidth().height(80.dp)
                .padding(top = 5.dp, end = 5.dp, start = 5.dp, bottom = 5.dp)                .clickable {
                    if (userauth.value) {
                        navController.navigate("Home")
                        scope.launch {
                            delay(6)
                            viewModel.logout()
                            sharePref.clearUserAuth(context)
                        }
                        Toast
                            .makeText(context, "Logout!!", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        Toast
                            .makeText(context, "You're not Logged In", Toast.LENGTH_SHORT)
                            .show()
                    }
                }
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "Logout",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Medium
                )
                Icon(
                    painter = painterResource(id = R.drawable.baseline_logout_24),
                    contentDescription = "Black_Arrow",
                    modifier = Modifier.padding(horizontal = 10.dp)
                )

            }
        }

        HorizontalDivider()


    }
}


@Composable
fun ProfileTop_Before(navController: NavController) {
    TextButton(
        onClick = {
            navController.navigate("login")
        }, modifier = Modifier
            .fillMaxWidth()
            .height(65.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.inverseSurface,
            contentColor = MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Text(
            text = stringResource(R.string.sign_up),
            fontSize = 24.sp
        )
    }

}

@Composable
fun ProfileTop_After(name: String, image: String) {
    Image(
        painter = if (image != null) {
            rememberAsyncImagePainter(image)
        } else {
            painterResource(R.drawable.blank_profile)
        },
        contentDescription = "Profile",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .height(130.dp)
            .width(130.dp)
            .clip(shape = RoundedCornerShape(100.dp))
    )
    Text(
        name,
        style = MaterialTheme.typography.headlineMedium,
        modifier = Modifier.padding(top = 5.dp)
    )
}

fun sendFeedBack(context: Context){
    val intent = Intent(Intent.ACTION_SENDTO).apply {
        data = Uri.parse("mailto:")
        putExtra(Intent.EXTRA_EMAIL,arrayOf("aman91252188@gmail.com"))
        putExtra(Intent.EXTRA_SUBJECT,"Feedback for Globalens")
    }
    try {
        context.startActivity(intent)
    }catch (e : Exception){
        Toast.makeText(context, "No email app found", Toast.LENGTH_SHORT).show()

    }
}