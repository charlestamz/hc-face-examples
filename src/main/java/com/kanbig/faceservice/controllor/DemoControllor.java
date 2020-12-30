package com.kanbig.faceservice.controllor;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.codec.binary.Base64;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.google.protobuf.ByteString;
import com.kanbig.faceservice.CompareFeatureRequest;
import com.kanbig.faceservice.CompareImageRequest;
import com.kanbig.faceservice.CompareOnDbRequest;
import com.kanbig.faceservice.FaceCompareResponse;
import com.kanbig.faceservice.FaceFindResponse;
import com.kanbig.faceservice.FaceResponse;
import com.kanbig.faceservice.FeatureResponse;
import com.kanbig.faceservice.Image;
import com.kanbig.faceservice.ImageRequest;
import com.kanbig.faceservice.ImageRequest.Builder;
import com.kanbig.faceservice.KanbigImageServiceGrpc;
import com.kanbig.faceservice.OpResponse;
import com.kanbig.faceservice.core.Model;
import com.kanbig.faceservice.core.Result;

@RestController
@RequestMapping("demo")
public class DemoControllor {
	private final int db = 1;
	private final int cam = 1;
	@Autowired
	KanbigImageServiceGrpc.KanbigImageServiceBlockingStub service;

	@PostMapping("enroll")
	public Result enroll(@RequestParam("files") MultipartFile files,
			@RequestParam(name = "personId", defaultValue = "") String personId,
			@RequestParam(name = "option", defaultValue = "-1") int option) {
		List<Model> res = new ArrayList<>();
		if (files != null) {
			try {
				long t1 = System.currentTimeMillis();
				Image image = Image.newBuilder().setFilename(files.getOriginalFilename())
						.setData(ByteString.copyFrom(files.getBytes())).build();
				long t2 = System.currentTimeMillis();
				personId = StringUtils.defaultIfBlank(personId, "test");
				Builder c = ImageRequest.newBuilder().setImageChunk(image).setCamId(cam).setListId(db)
						.setPersonID(personId);
				if (option > -1) {
					c.setOption(option);
				}
				OpResponse data = service.enroll(c.build());
				long t3 = System.currentTimeMillis();
				res.add(Model.build().add("fileName", files.getOriginalFilename()).add("personId", personId)
						.add("result", data.toString()).add("t1", t1).add("t2", t2).add("t3", t3));
			} catch (Exception e) {
				return Result.error(e.getMessage());
			}

		}
		return Result.success(res);
	}

	@PostMapping("getFeatures")
	public Result getFeatures(@RequestParam("files") MultipartFile[] files,
			@RequestParam(name = "option", defaultValue = "-1") int option) {
		List<Model> res = new ArrayList<>();
		if (files != null) {
			for (MultipartFile file : files) {
				try {
					long t1 = System.currentTimeMillis();
					Image image = Image.newBuilder().setFilename(file.getOriginalFilename())
							.setData(ByteString.copyFrom(file.getBytes())).build();
					long t2 = System.currentTimeMillis();
					Builder c = ImageRequest.newBuilder().setImageChunk(image).setCamId(1).setListId(1);
					if (option > -1) {
						c.setOption(option);
					}
					FeatureResponse data = service.getFeature(c.build());
					long t3 = System.currentTimeMillis();

					res.add(Model.build().add("fileName", file.getOriginalFilename()).add("result", data.toString())
							.add("feature", data.getFeature() == null ? null : data.getFeature().toStringUtf8())
							.add("t1", t1).add("t2", t2).add("t3", t3));
				} catch (Exception e) {
					return Result.error(e.getMessage());
				}
			}
		}
		return Result.success(res);
	}

	@PostMapping("compareOnDb")
	public Result compareOnDb(@RequestParam("files") MultipartFile files,
			@RequestParam(name = "faceId", defaultValue = "") int faceId,
			@RequestParam(name = "option", defaultValue = "-1") int option) {
		List<Model> res = new ArrayList<>();
		if (files != null) {
			try {
				long t1 = System.currentTimeMillis();
				Image image = Image.newBuilder().setFilename(files.getOriginalFilename())
						.setData(ByteString.copyFrom(files.getBytes())).build();
				long t2 = System.currentTimeMillis();
				com.kanbig.faceservice.CompareOnDbRequest.Builder c = CompareOnDbRequest.newBuilder().setImg(image)
						.setFaceId(faceId).setListId(db);
				if (option > -1) {
					c.setOption(option);
				}
				FaceCompareResponse data = service.compareOnDb(c.build());
				long t3 = System.currentTimeMillis();
				res.add(Model.build().add("fileName", files.getOriginalFilename()).add("faceId", faceId)
						.add("result", data.toString()).add("t1", t1).add("t2", t2).add("t3", t3));
			} catch (Exception e) {
				return Result.error(e.getMessage());
			}
		}
		return Result.success(res);
	}

	@PostMapping("compareImage")
	public Result compareImage(@RequestParam("file1") MultipartFile file1, @RequestParam("file2") MultipartFile file2,
			@RequestParam(name = "option", defaultValue = "-1") int option) {
		List<Model> res = new ArrayList<>();
		if (file1 != null && file2 != null) {
			try {
				long t1 = System.currentTimeMillis();
				Image image1 = Image.newBuilder().setFilename(file1.getOriginalFilename())
						.setData(ByteString.copyFrom(file1.getBytes())).build();
				Image image2 = Image.newBuilder().setFilename(file2.getOriginalFilename())
						.setData(ByteString.copyFrom(file2.getBytes())).build();
				long t2 = System.currentTimeMillis();
				com.kanbig.faceservice.CompareImageRequest.Builder c = CompareImageRequest.newBuilder().setImg(image1)
						.setOrigin(image2);
				if (option > -1) {
					c.setOption(option);
				}
				FaceCompareResponse data = service.compareImage(c.build());
				long t3 = System.currentTimeMillis();
				res.add(Model.build().add("fileName1", file1.getOriginalFilename())
						.add("fileName2", file2.getOriginalFilename()).add("result", data.toString()).add("t1", t1)
						.add("t2", t2).add("t3", t3));
			} catch (Exception e) {
				return Result.error(e.getMessage());
			}
		}
		return Result.success(res);
	}

	@PostMapping("searchFace")
	public Result searchFace(@RequestParam("files") MultipartFile files,
			@RequestParam(name = "topn", defaultValue = "-1") int topn) {
		List<Model> res = new ArrayList<>();
		if (files != null) {
			try {
				long t1 = System.currentTimeMillis();
				Image image = Image.newBuilder().setFilename(files.getOriginalFilename())
						.setData(ByteString.copyFrom(files.getBytes())).build();
				long t2 = System.currentTimeMillis();
				Builder c = ImageRequest.newBuilder().setImageChunk(image).setCamId(cam).setListId(db);
				if (topn > -1) {
					c.setTopn(topn);
				}
				FaceResponse data = service.searchFace(c.build());
				long t3 = System.currentTimeMillis();
				res.add(Model.build().add("fileName", files.getOriginalFilename()).add("result", data.toString())
						.add("t1", t1).add("t2", t2).add("t3", t3));
			} catch (Exception e) {
				return Result.error(e.getMessage());
			}
		}
		return Result.success(res);
	}

	@PostMapping("identifyFace")
	public Result identifyFace(@RequestParam("files") MultipartFile files,
			@RequestParam(name = "personId", defaultValue = "") String personId,
			@RequestParam(name = "option", defaultValue = "-1") int option) {
		List<Model> res = new ArrayList<>();
		personId = StringUtils.defaultIfBlank(personId, "test");
		if (files != null) {
			try {
				long t1 = System.currentTimeMillis();
				Image image = Image.newBuilder().setFilename(files.getOriginalFilename())
						.setData(ByteString.copyFrom(files.getBytes())).build();
				long t2 = System.currentTimeMillis();
				Builder c = ImageRequest.newBuilder().setImageChunk(image).setPersonID(personId).setCamId(cam)
						.setListId(db);
				if (option > -1) {
					c.setOption(option);
				}
				FaceResponse data = service.identifyFace(c.build());
				long t3 = System.currentTimeMillis();
				res.add(Model.build().add("fileName", files.getOriginalFilename()).add("result", data.toString())
						.add("t1", t1).add("t2", t2).add("t3", t3));
			} catch (Exception e) {
				return Result.error(e.getMessage());
			}
		}
		return Result.success(res);
	}

	@PostMapping("findFaces")
	public Result findFaces(@RequestParam("files") MultipartFile files,
			@RequestParam(name = "option", defaultValue = "-1") int option) {
		List<Model> res = new ArrayList<>();
		if (files != null) {
			try {
				long t1 = System.currentTimeMillis();
				Image image = Image.newBuilder().setFilename(files.getOriginalFilename())
						.setData(ByteString.copyFrom(files.getBytes())).build();
				long t2 = System.currentTimeMillis();
				Builder c = ImageRequest.newBuilder().setImageChunk(image).setCamId(cam).setListId(db);
				if (option > -1) {
					c.setOption(option);
				}
				FaceFindResponse data = service.findFaces(c.build());
				long t3 = System.currentTimeMillis();
				res.add(Model.build().add("fileName", files.getOriginalFilename()).add("result", data.toString())
						.add("t1", t1).add("t2", t2).add("t3", t3));
			} catch (Exception e) {
				return Result.error(e.getMessage());
			}
		}
		return Result.success(res);
	}

	@PostMapping("compareFeature")
	public Result compareFeature(@RequestParam("file") MultipartFile file,
			@RequestParam(name = "feature") String feature,
			@RequestParam(name = "option", defaultValue = "9") int option) {
		List<Model> res = new ArrayList<>();
		if (file != null) {
			try {
				long t1 = System.currentTimeMillis();
				Image image = Image.newBuilder().setFilename(file.getOriginalFilename())
						.setData(ByteString.copyFrom(file.getBytes())).build();
				long t2 = System.currentTimeMillis();
				CompareFeatureRequest request = CompareFeatureRequest.newBuilder()
						.setFeature(ByteString.copyFrom(Base64.decodeBase64(feature))).setImg(image).setOption(option)
						.build();
				FaceCompareResponse data = service.compareFeature(request);
				long t3 = System.currentTimeMillis();
				return Result.success(Model.build().add("fileName", file.getOriginalFilename())
						.add("result", data.toString()).add("t1", t1).add("t2", t2).add("t3", t3));
			} catch (Exception e) {
				return Result.error(e.getMessage());
			}
		}
		return Result.success(res);
	}

	@PostMapping("getFeature")
	public Result getFeature(@RequestParam("file") MultipartFile file,
			@RequestParam(name = "option", defaultValue = "9") int option) {
		List<Model> res = new ArrayList<>();
		if (file != null) {
			try {
				long t1 = System.currentTimeMillis();
				Image image = Image.newBuilder().setFilename(file.getOriginalFilename())
						.setData(ByteString.copyFrom(file.getBytes())).build();
				long t2 = System.currentTimeMillis();
				Builder c = ImageRequest.newBuilder().setImageChunk(image).setCamId(1).setListId(1);
				if (option > -1) {
					c.setOption(option);
				}
				FeatureResponse data = service.getFeature(c.build());
				long t3 = System.currentTimeMillis();
				return Result.success(
						Model.build().add("fileName", file.getOriginalFilename()).add("result", data.toString())
								.add("feature",
										data.getFeature() == null ? null
												: Base64.encodeBase64String(data.getFeature().toByteArray()))
								.add("t1", t1).add("t2", t2).add("t3", t3));
			} catch (Exception e) {
				return Result.error(e.getMessage());
			}
		}
		return Result.success(res);
	}

}
