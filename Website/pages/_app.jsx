import '/styles/globals.css'

<Head>
<link rel="shortcut icon" href="/favicons/favicon.ico" />
<link rel="apple-touch-icon" sizes="180x180" href="/favicons/apple-touch-icon.png" />
<link rel="icon" type="image/png" sizes="32x32" href="/favicons/favicon-32x32.png"/>
<link rel="icon" type="image/png" sizes="16x16" href="/favicons/favicon-16x16.png"/>
</Head>

export default function App({ Component, pageProps }) {
  return <Component {...pageProps} />
}
