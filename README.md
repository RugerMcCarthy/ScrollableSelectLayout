## ScrollableSelectLayout
A scrollable selector for Jetpack Compose，can be applied to some scenes that require scrolling selection

## Parameter Introduction
```kotlin
/**
 * @param items: A list contains sub-elements' info
 * @param scrollableSelectState: Through the state can get the info of selected item
 * @param itemHeight: Manually input the height of the sub-elements
 * @param modifier: Used to decorate this component
 * @param visibleAmount: Specify the number of sub-elements that can be displayed
 * @param selectionLineStyle: Specify the style of the selection line
 * @param content: Specify the layout style of sub-elements, You can make the selected item display different styles according to the state.
 */
fun <E> ScrollableSelectLayout(
    items: List<E>,
    scrollableSelectState: ScrollableSelectState = rememberScrollableSelectState(),
    itemHeight: Dp,
    modifier: Modifier = Modifier,
    visibleAmount: Int = 3,
    selectionLineStyle: SelectionLineStyle = SelectionLineStyle.Default,
    content: @Composable RowScope.(item: E, Boolean) -> Unit
)
```
## :camera_flash: Screenshots

<!-- You can add more screenshots here if you like -->
<img src="/samples/slide_select_bar.gif" width="260">

## License

```
Copyright 2020 The Android Open Source Project

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

