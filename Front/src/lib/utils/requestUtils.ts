import Cookies from 'js-cookie';

const API_URL: string = 'http://localhost:8082/api'

export const canIAccess = async (): Promise<boolean> => {
    let token: any = Cookies.get('jwt')
    if (!token) return false
    let access: boolean = false;
    const response = await fetch(API_URL+"/users/token", {
        method: 'GET',
        mode: 'cors',
        headers: {
            Authorization: 'Bearer '+token
        }
    })
    access = response.status === 200
    return access
}

export const logAndSetToken = async (username: string, password: string): Promise<string | null> => {
    if (await canIAccess()) return null;
    let postError: string | null = null;
    const data: string = JSON.stringify({
        'name' : username,
        'password' : password,
    });
    
    
    const response = await fetch(API_URL+"/users/signIn",
    {
        method: 'POST',
        mode: 'cors',
        headers: {
            'Content-Type': 'application/json',
        },
        body: data
    })
    const {token, message} = await response.json()
    if (response.status === 202) Cookies.set('jwt', token)
    else postError = message
    console.log(postError)
    return postError
}

export const getWithJWT = async (url: string, expectedCode: number): Promise<[any, any]> => {
    if (!await canIAccess()) {
        return [null, {message : 'invalid token'}]
    }
    let response
    try {
        response = await fetch(url,
            {
                method: 'GET',
                mode: 'cors',
                headers: {
                    Authorization: 'Bearer '+Cookies.get('jwt')
                }
            })
    } catch (e) {
        return [null, {message: 'Server error'}]
    }
    let jsonResponse
    try {
        jsonResponse = await response.json()
    } catch(e) {
        jsonResponse = null
    }
    if (response.status === expectedCode) {
        return [jsonResponse, null]
    } 
    if (response.status === 400 || response.status === 401) {
        return [null, {message: 'Server error'}]
    }
    return [null, jsonResponse]
}

export const postWithJWT = async (url: string, expectedCode: number, body: any) => {
    if (!await canIAccess()) {
        return [null, {message : 'invalid token'}]
    }
    const response = await fetch(url,
    {
        method: 'POST',
        mode: 'cors',
        headers: {
            Authorization: 'Bearer '+Cookies.get('jwt'),
            'Content-Type': 'application/json'
        },
        body
    })
    let jsonResponse
    try {
        jsonResponse = await response.json()
    } catch(e) {
        jsonResponse = {}
    }
    if (response.status === expectedCode) {
        return [jsonResponse, null]
    }
    return [null, jsonResponse]
}

export const deleteWithJWT = async (url: string, expectedCode: number): Promise<[any, any]> => {
    if (!await canIAccess()) {
        return [null, {message : 'invalid token'}]
    }
    let response
    try {
        response = await fetch(url,
            {
                method: 'DELETE',
                mode: 'cors',
                headers: {
                    Authorization: 'Bearer '+Cookies.get('jwt')
                }
            })
    } catch (e) {
        return [null, {message: 'Server error'}]
    }
    let jsonResponse
    try {
        jsonResponse = await response.json()
    } catch(e) {
        jsonResponse = {}
    }
    if (response.status === expectedCode) {
        return [jsonResponse, null]
    }
    if (response.status === 400 || response.status === 401) {
        return [null, {message: 'Server error'}]
    }
    return [null, jsonResponse]
}

