export const portValidator = (port: string) : boolean => {
    return port !== null && port.length !== 0 && !isNaN(+port) && +port > 0 && +port < 65536;
}

export const gatewayValidator = (gateway: string) : boolean =>  {
    const ipRegex = new RegExp(/^(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)\.(25[0-5]|2[0-4][0-9]|[01]?[0-9][0-9]?)$/);
    return ipRegex.test(gateway);
}

export const netmaskValidator = (netmask: string) : boolean =>  {
    const maskRegex = new RegExp(/^(255)\.(0|128|192|224|240|248|252|254|255)\.(0|128|192|224|240|248|252|254|255)\.(0|128|192|224|240|248|252|254|255)$/);
    return maskRegex.test(netmask);
}

export const emailValidator = (email: string) : boolean => {
    const emailRegex = new RegExp(/^[\w!#$%&'*+/=?`{|}~^-]+(?:\.[\w!#$%&'*+/=?`{|}~^-]+)*@(?:[a-zA-Z0-9-]+\.)+[a-zA-Z]{2,6}$/);
    return emailRegex.test(email)
}

export const fqdnValidator = (fqdn: string) : boolean => {
    const fqdnRegex = new RegExp(/^(?!:\/\/)(?=.{1,255}$)((.{1,63}\.){1,127}(?![0-9]*$)[a-z0-9-]+\.?)$/);
    return fqdnRegex.test(fqdn)
}

