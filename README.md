# UV Index Widget
This app provides a widget displaying the current UV-Index at your location. The widget presents the UV-Index and a color in the background following this rule:

<style>
low { color: #5CDF47 }
moderate { color: #FFEB3B }
high { color: #FA8B02 }
veryhigh { color: #D80020 }
extreme { color: #A80080 }
</style>

Intensity | UV Index
---|---
<low>Low</low> | 0-2
<moderate>Moderate</moderate> | 3-5
<high>High</high> | 6-7
<veryhigh>Very High</veryhigh> | 8-10
<extreme>Extreme</extreme> | 11+

## Reference
* [OpenWeatherMap](https://openweathermap.org/api/one-call-api)
* [Retrofit](https://square.github.io/retrofit/)
* [Koin](https://insert-koin.io/docs/quickstart/android/)
