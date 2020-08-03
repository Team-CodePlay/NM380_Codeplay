import json
import os

import firebase_admin
from firebase_admin import db
from flask import Flask
from flask import request
from google.cloud import videointelligence
from google.oauth2 import service_account

credentials_raw = os.environ.get("GOOGLE_APPLICATION_CREDENTIALS")

service_account_info = json.loads(credentials_raw)
credentials = service_account.Credentials.from_service_account_info(
    service_account_info)

# service_account_info2 = json.loads(credentials_raw)
# credentials2 = service_account.Credentials.from_service_account_info(
#     service_account_info)


credentials_raw2 = json.loads(os.environ.get("FIREBASE_CREDENTIALS"))

firebase_creds = firebase_admin.credentials.Certificate(credentials_raw2)
firebase_admin.initialize_app(firebase_creds, {
    'databaseURL': 'https://geoplay-codeplay.firebaseio.com/'
})

app = Flask(__name__)


def get_video_intelligence(gs_uri):
    """ Detects labels given a GCS path. """
    video_client = videointelligence.VideoIntelligenceServiceClient(credentials=credentials)
    features = [videointelligence.enums.Feature.LABEL_DETECTION]

    mode = videointelligence.enums.LabelDetectionMode.SHOT_AND_FRAME_MODE
    config = videointelligence.types.LabelDetectionConfig(label_detection_mode=mode)
    context = videointelligence.types.VideoContext(label_detection_config=config)

    operation = video_client.annotate_video(
        input_uri=gs_uri, features=features, video_context=context
    )
    print("\nProcessing video for label annotations:")

    result = operation.result(timeout=180)
    print("\nFinished processing.")

    # Process video/segment level label annotations
    segment_labels = result.annotation_results[0].segment_label_annotations
    labels = []
    for i, segment_label in enumerate(segment_labels):
        # print("Video label description: {}".format(segment_label.entity.description))
        # for category_entity in segment_label.category_entities:
        #     break
        #     # print(
        #     #     "\tLabel category description: {}".format(category_entity.description)
        #     # )
        # else:
        #     continue

        for i, segment in enumerate(segment_label.segments):
            start_time = (
                    segment.segment.start_time_offset.seconds
                    + segment.segment.start_time_offset.nanos / 1e9
            )
            end_time = (
                    segment.segment.end_time_offset.seconds
                    + segment.segment.end_time_offset.nanos / 1e9
            )
            positions = "{}s to {}s".format(start_time, end_time)
            confidence = segment.confidence
            # print("\tSegment {}: {}".format(i, positions))
            # print("\tConfidence: {}".format(confidence))

            labels.append('{} : {}'.format(segment_label.entity.description, confidence))
            break

    # # Process shot level label annotations
    # shot_labels = result.annotation_results[0].shot_label_annotations
    # for i, shot_label in enumerate(shot_labels):
    #     print("Shot label description: {}".format(shot_label.entity.description))
    #     for category_entity in shot_label.category_entities:
    #         print(
    #             "\tLabel category description: {}".format(category_entity.description)
    #         )
    #
    #     for i, shot in enumerate(shot_label.segments):
    #         start_time = (
    #                 shot.segment.start_time_offset.seconds
    #                 + shot.segment.start_time_offset.nanos / 1e9
    #         )
    #         end_time = (
    #                 shot.segment.end_time_offset.seconds
    #                 + shot.segment.end_time_offset.nanos / 1e9
    #         )
    #         positions = "{}s to {}s".format(start_time, end_time)
    #         confidence = shot.confidence
    #         print("\tSegment {}: {}".format(i, positions))
    #         print("\tConfidence: {}".format(confidence))
    #     print("\n")

    # Process frame level label annotations
    frame_labels = result.annotation_results[0].frame_label_annotations
    frame_lab = []
    for i, frame_label in enumerate(frame_labels):
        # print("Frame label description: {}".format(frame_label.entity.description))
        # for category_entity in frame_label.category_entities:
        #     print(
        #         "\tLabel category description: {}".format(category_entity.description)
        #     )

        # Each frame_label_annotation has many frames,
        # here we print information only about the first frame.
        frame = frame_label.frames[0]
        time_offset = frame.time_offset.seconds + frame.time_offset.nanos / 1e9

        frame_lab.append(
            (
                int(frame.time_offset.seconds),
                {"label": frame_label.entity.description, "confidence": frame.confidence}
            )
        )

        # print("\tFirst frame time offset: {}s".format(time_offset))
        # print("\tFirst frame confidence: {}".format(frame.confidence))
        # print("\n")

    return labels, frame_lab


@app.route('/')
def object_detect():
    db_path = request.args.get('db_path', '')
    # sample db_path = videos/92skWnE7fxSRpazoHmQeFlBSssX2/-MDhoTowjzImvZYcsmCM

    if db_path == '':
        return "Bad request " + db_path

    ref = db.reference(db_path + '/video_path')
    gs_uri = str(ref.get())

    ref = db.reference(db_path + '/geotags')
    geotags = ref.get()

    # return json.dumps(geotags)
    # print()

    labels, frame_labels = get_video_intelligence(gs_uri)
    print(len(labels), len(frame_labels))

    frame_labels = sorted(frame_labels, key=lambda x: x[0])


    index = 0
    for geotag in geotags:
        labels_to_add = []

        while index < len(frame_labels) and int(geotag['video_time']/1000) != int(frame_labels[index][0]) :
            index += 1

        while index < len(frame_labels) and int(geotag['video_time']/1000) == int(frame_labels[index][0]) :
            if frame_labels[index][1]['confidence'] > 0.85:
                labels_to_add.append(frame_labels[index][1])
            index += 1

        geotag['labels'] = labels_to_add


    # index = 0
    # for second, data in frame_labels:
    #     t = second * 1000
    #
    #     labels_to_add = []
    #
    #     try:
    #         while t > geotags[index]['video_time'] and index < len(geotags):
    #             index += 1
    #
    #         if data['confidence'] > 0.85:
    #             labels_to_add.append(data)
    #             # geotags[index]['label'] = data['label']
    #             # geotags[index]['confidence'] = data['confidence']
    #             # labels
    #     except Exception:
    #         pass
    #
    #     geotags[index]['labels'] = labels_to_add


    # ref = db.reference(db_path + '/geotags')
    # ref.set(geotags)
    #
    # ref = db.reference(db_path + '/labels')
    # ref.set(labels)

    return str(geotags) + '\n' + str(labels) + "\n\n" + str(frame_labels)

