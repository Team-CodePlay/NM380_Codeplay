export const kmlStart1 = `<?xml version="1.0" encoding="UTF-8"?>
<kml xmlns="http://earth.google.com/kml/2.2">
    <Document>
        <name>`
export const kmlStart2 = `</name>
        <Style id="paddle-a">
            <IconStyle>
                <Icon>
                    <href>http://maps.google.com/mapfiles/kml/paddle/A.png</href>
                </Icon>
                <hotSpot x="32" xunits="pixels" y="1" yunits="pixels" />
            </IconStyle>
        </Style>
        <Style id="paddle-b">
            <IconStyle>
                <Icon>
                    <href>http://maps.google.com/mapfiles/kml/paddle/B.png</href>
                </Icon>
                <hotSpot x="32" xunits="pixels" y="1" yunits="pixels" />
            </IconStyle>
        </Style>
        <Style id="hiker-icon">
            <IconStyle>
                <Icon>
                    <href>http://maps.google.com/mapfiles/ms/icons/hiker.png</href>
                </Icon>
                <hotSpot x="0" xunits="fraction" y=".5" yunits="fraction" />
            </IconStyle>
        </Style>
        <Style id="check-hide-children">
            <ListStyle>
                <listItemType>checkHideChildren</listItemType>
            </ListStyle>
        </Style>
        <styleUrl>#check-hide-children</styleUrl>
        `

export const kmlEnd = `
</Document>
</kml>`


    