import Head from 'next/head'
import { Inter } from '@next/font/google'
import Header from '../components/Header'
import Contribute from '../components/Contribute'
import Demo from '../components/Demo'
import Footer from '../components/Footer'
import MainSection from '@/components/MainSection'
import Releases from '@/components/Releases'


const inter = Inter({ subsets: ['latin'] })

export default function Home(props) {
  return (
    <>
      <Head>
        <title>Drifty</title>
        <meta name="description" content="Interactive File Downloader System" />
        <meta name="viewport" content="width=device-width, initial-scale=1" />
        <link rel="apple-touch-icon" sizes="180x180" href="favicons/apple-touch-icon.png" />
        <link rel="icon" type="image/png" sizes="32x32" href="favicons/favicon-32x32.png" />
        <link rel="icon" type="image/png" sizes="16x16" href="favicons/favicon-16x16.png" />
        <link rel="manifest" href="favicons/site.webmanifest" />
      </Head>
      <Header props={"bg-top"} />
      <MainSection />
      <Contribute props={props.contribs}/>
      <Releases props={props.releases}/>
      <Demo />
      <Footer />
    </>
  )
}
async function getContibs(username) {
  const res = await fetch('https://api.github.com/repos/SaptarshiSarkar12/Drifty/contributors',{method:'GET'})
  return res.json();
}

async function getReleases(username) {
  const res = await fetch('https://api.github.com/repos/SaptarshiSarkar12/Drifty/releases');
  return res.json();
}
export async function getServerSideProps(){
  const contribsdata=getContibs();
  const releasesdata=getReleases();
  const [contrib,release]= await Promise.all([contribsdata,releasesdata]);
  return {
    props:{
      revalidate:3600,
      contribs:{contrib},
      releases:{release}
    }
  }
}
