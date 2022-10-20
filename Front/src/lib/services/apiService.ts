export async function makePost(data: string, url: string) {

    const options = {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json'
        },
        body: data
    }

    return  fetch(url, options).then(res => res.json());

}
