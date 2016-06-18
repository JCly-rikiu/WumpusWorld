class LocationController < ApplicationController

  include Map_data

  def location
    nickname = params[:nickname]
    latitude = params[:latitude]
    longitude = params[:longitude]

    least_distance = 1000000
    rnt_pos = -1
    cnt = -1

    get_positions.each do |pos|
      distance = cal_distance(latitude, pos[:latitude], longitude, pos[:longitude])

      cnt += 1

      next if distance > 0.00125
      rnt_pos = cnt if distance < least_distance
    end

    if rnt_pos == -1
      render json: { status: 1 }
    else
      render json: { status: 0, data: rnt_pos }
    end
  end

  private

  def cal_distance(x1, x2, y1, y2)
    Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2))
  end
end
