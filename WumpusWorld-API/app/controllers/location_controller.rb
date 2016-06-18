class LocationController < ApplicationController

  include Map_data

  def location
    nickname = params[:nickname]
    if Player.exists?(name: nickname)
      player = Player.find_by_name(nickname)
      unless player.is_playing
        render json: { status: 1 }
        return
      end
    end

    latitude = params[:latitude]
    longitude = params[:longitude]

    least_distance = 1000000
    rnt_pos = -1
    cnt = -1

    get_positions.each do |pos|
      distance = cal_distance(latitude, pos[:latitude], longitude, pos[:longitude])

      cnt += 1

      next if distance > 0.00125
      if distance < least_distance
        least_distance = distance
        rnt_pos = cnt
      end
    end

    if rnt_pos == -1
      render json: { status: 1 }
    else
      render json: { status: 0, data: rnt_pos }
    end
  end

  def go
    nickname = params[:nickname]
    pos = params[:pos].to_i

    if Player.exists?(name: nickname)
      player = Player.find_by_name(nickname)
      state = player.state

      num = -1
      state.length.times do
        num += 1
        char = state[num]

        if char == 'C'
          state[num] = '1'
          break
        end
      end

      if state[pos] == 'W'
        state[pos] = 'w'
        player.update(is_playing: false, state: state)
        render json: { status: 1, pos: pos, code: 0, msg: "You're eaten." }
      elsif state[pos] == 'P'
        state[pos] = 'p'
        player.update(is_playing: false, state: state)
        render json: { status: 1, pos: pos, code: 1, msg: "You fell down." }
      else
        state[pos] = 'C'
        player.update(state: state)
        render json: { status: 0, data: parse_states(player.state) }
      end
    else
      render json: { status: 2 }
    end
  end

  def shoot
    nickname = params[:nickname]
    pos = params[:pos].to_i

    if Player.exists?(name: nickname)
      player = Player.find_by_name(nickname)
      state = player.state

      if state[pos] == 'W'
        state[pos] = '0'

        player.update(state: state)

        render json: { status: 0, data: parse_states(player.state) }
      else
        render json: { status: 1, data: parse_states(player.state) }
      end
    end
  end

  private

  def cal_distance(x1, x2, y1, y2)
    Math.sqrt((x1 - x2) * (x1 - x2) + (y1 - y2) * (y1 - y2))
  end
end
